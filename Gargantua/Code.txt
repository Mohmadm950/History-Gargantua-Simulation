import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

    private long window;
    private int shaderProgram;

    // Uniforms
    private int u_time, u_mouse, u_zoom, u_resolution;

    // Kamera / Steuerung
    private float camYaw = 0.0f;
    private float camPitch = 15.0f; // Startet leicht geneigt, damit man die Scheibe besser sieht
    private float zoom = 1.0f;

    private double lastMouseX, lastMouseY;
    private boolean leftMousePressed = false;

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        window = glfwCreateWindow(1280, 720, "Gargantua - Realistisches 3D Schwarzes Loch", NULL, NULL);
        if (window == NULL) throw new RuntimeException("Failed to create window");

        // === Maus Steuerung ===
        glfwSetCursorPosCallback(window, (win, xpos, ypos) -> {
            if (leftMousePressed) {
                float dx = (float) (xpos - lastMouseX);
                float dy = (float) (ypos - lastMouseY);
                camYaw -= dx * 0.35f;   // Bewegungsrichtung angepasst
                camPitch += dy * 0.35f;
                camPitch = Math.max(-80f, Math.min(80f, camPitch));
            }
            lastMouseX = xpos;
            lastMouseY = ypos;
        });

        glfwSetMouseButtonCallback(window, (win, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                leftMousePressed = (action == GLFW_PRESS);
            }
        });

        glfwSetScrollCallback(window, (win, xoffset, yoffset) -> {
            zoom -= (float) yoffset * 0.08f;
            zoom = Math.max(0.4f, Math.min(3.5f, zoom));
        });

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        GL.createCapabilities();

        shaderProgram = createAdvancedShader();
        u_time = glGetUniformLocation(shaderProgram, "u_time");
        u_mouse = glGetUniformLocation(shaderProgram, "u_mouse");
        u_zoom = glGetUniformLocation(shaderProgram, "u_zoom");
        u_resolution = glGetUniformLocation(shaderProgram, "u_resolution");

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glfwShowWindow(window);
    }

    private int createAdvancedShader() {
        String vertex = """
            #version 330 core
            layout(location = 0) in vec2 aPos;
            out vec2 uv;
            void main() {
                gl_Position = vec4(aPos, 0.0, 1.0);
                uv = aPos * 0.5 + 0.5;
            }
            """;

        // ECHTER 3D Volumetric Raymarching Shader
        String fragment = """
            #version 330 core
            in vec2 uv;
            out vec4 fragColor;

            uniform float u_time;
            uniform vec2 u_mouse;
            uniform float u_zoom;
            uniform vec2 u_resolution;

            // Pseudo-Random Number Generator für Sterne
            float hash(float n) { return fract(sin(n) * 43758.5453123); }
            
            // 3D Rotationsmatrizen für die Kamera
            mat3 rotX(float a) { float c=cos(a), s=sin(a); return mat3(1,0,0, 0,c,-s, 0,s,c); }
            mat3 rotY(float a) { float c=cos(a), s=sin(a); return mat3(c,0,s, 0,1,0, -s,0,c); }

            void main() {
                // Bildschirmkoordinaten zentrieren und Aspect Ratio anpassen
                vec2 p = (uv * 2.0 - 1.0);
                p.x *= u_resolution.x / u_resolution.y;
                p /= u_zoom;

                // Kamera-Winkel aus Maus-Input
                float yaw = radians(u_mouse.x);
                float pitch = radians(u_mouse.y);

                // Ray Setup (Kamera Position und Strahlrichtung)
                vec3 ro = vec3(0.0, 0.0, -5.5); // Etwas näher dran
                vec3 rd = normalize(vec3(p, 1.8)); // Field of View (1.8 macht es filmischer)

                // Kamera drehen
                ro = rotX(pitch) * rotY(yaw) * ro;
                rd = rotX(pitch) * rotY(yaw) * rd;

                vec3 col = vec3(0.0);
                vec3 accCol = vec3(0.0);
                float density = 0.0;
                
                vec3 pos = ro;
                vec3 dir = rd;
                float dt = 0.06; // Raymarching Schrittweite
                
                bool hitEventHorizon = false;

                // --- ECHTES RAYMARCHING MIT GRAVITATION ---
                // Wir schießen einen Lichtstrahl in die Szene und berechnen jeden Schritt
                for(int i = 0; i < 140; i++) {
                    float r2 = dot(pos, pos);
                    float r = sqrt(r2);
                    
                    // 1. Ereignishorizont (Licht wird geschluckt)
                    if(r < 0.85) {
                        hitEventHorizon = true;
                        break;
                    }

                    // 2. Gravitationslinseneffekt (Lichtkrümmung!)
                    // Die Schwerkraft zieht den Lichtstrahl in Richtung Ursprung
                    vec3 gravity = -pos / (r2 * r) * 0.45; // 0.45 ist die "Masse"
                    dir = normalize(dir + gravity * dt);

                    // 3. Akkretionsscheibe (Volumetrischer Nebel)
                    if(r > 1.2 && r < 4.5) {
                        float distFromPlane = abs(pos.y);
                        float diskThickness = 0.12 * (r - 0.5); // Wird nach außen etwas dicker
                        
                        if (distFromPlane < diskThickness) {
                            // Dichteberechnung innerhalb der Scheibe
                            float fadeY = 1.0 - smoothstep(0.0, diskThickness, distFromPlane);
                            float fadeR = smoothstep(4.5, 3.0, r) * smoothstep(1.2, 1.6, r);
                            
                            // Bewegung und Struktur in der Scheibe
                            float angle = atan(pos.z, pos.x);
                            float swirl = sin(angle * 12.0 - u_time * 2.5 + r * 5.0);
                            swirl = smoothstep(-1.0, 1.0, swirl);
                            
                            float localDensity = fadeY * fadeR * (0.4 + 0.6 * swirl);
                            
                            // Farbverlauf: Sehr heiß innen (Weiß/Blau), kühler außen (Orange/Rot)
                            vec3 colorGlow = mix(vec3(0.9, 0.2, 0.05), vec3(1.0, 0.7, 0.3), smoothstep(4.0, 1.5, r));
                            colorGlow = mix(colorGlow, vec3(1.0, 0.95, 0.9), smoothstep(1.8, 1.2, r)); 
                            
                            // 4. Doppler-Effekt (Wie im Film: Die auf uns zukommende Seite leuchtet heller!)
                            vec3 vel = normalize(vec3(-pos.z, 0.0, pos.x)); // Rotationsvektor
                            float doppler = dot(dir, vel);
                            float dopplerFactor = pow(1.0 + doppler * 0.55, 3.0);
                            
                            // Farbe und Dichte aufaddieren
                            accCol += colorGlow * localDensity * dopplerFactor * dt * 4.5;
                            density += localDensity * dt * 2.5;
                        }
                    }
                    // Gehe einen Schritt weiter
                    pos += dir * dt;
                }

                // --- HINTERGRUND & STERNE ---
                if(!hitEventHorizon) {
                    // Wir nutzen die *verbogene* Strahlrichtung (dir) für die Sterne!
                    // Dadurch werden die Sterne vom Schwarzen Loch optisch herumgezogen (Lensing)
                    vec2 starUV = vec2(atan(dir.z, dir.x), asin(dir.y));
                    float starVal = 0.0;
                    
                    // Mehrere Layer von Sternen generieren
                    for(int j = 0; j < 3; j++) {
                        float scale = 35.0 + float(j) * 25.0;
                        vec2 id = floor(starUV * scale);
                        vec2 localUV = fract(starUV * scale) - 0.5;
                        
                        // Zufällige Position pro Grid-Zelle
                        float randX = hash(id.x * 12.3 + id.y * 45.6);
                        float randY = hash(id.x * 78.9 + id.y * 12.3);
                        vec2 starPos = vec2(randX, randY) - 0.5;
                        
                        float dist = length(localUV - starPos * 0.7);
                        float brightness = hash(id.x * 34.5 + id.y * 67.8);
                        
                        // Leichtes Funkeln
                        brightness *= (0.6 + 0.4 * sin(u_time * 1.5 + brightness * 50.0));
                        
                        // Sterne nur als winzige Punkte zeichnen
                        starVal += smoothstep(0.04, 0.01, dist) * pow(brightness, 2.0);
                    }
                    
                    vec3 starsCol = vec3(0.9, 0.95, 1.0) * starVal;
                    
                    // Sterne hinter die Akkretionsscheibe blenden (basierend auf der Dichte)
                    col += starsCol * exp(-density * 2.0);
                }

                // Die leuchtende Scheibe obendrauf legen
                col += accCol;

                // --- POST-PROCESSING (Filmic Tonemapping) ---
                // Macht die Farben satter und verhindert, dass helle Stellen flach wirken (ACES fit)
                col = (col * (2.51 * col + 0.03)) / (col * (2.43 * col + 0.59) + 0.14);
                
                fragColor = vec4(col, 1.0);
            }
            """;

        int vs = compileShader(GL_VERTEX_SHADER, vertex);
        int fs = compileShader(GL_FRAGMENT_SHADER, fragment);

        int program = glCreateProgram();
        glAttachShader(program, vs);
        glAttachShader(program, fs);
        glLinkProgram(program);
        glDeleteShader(vs);
        glDeleteShader(fs);

        return program;
    }

    private int compileShader(int type, String source) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Shader compilation failed:\n" + glGetShaderInfoLog(shader));
        }
        return shader;
    }

    private void loop() {
        float[] quad = {-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f};
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, quad, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        glUseProgram(shaderProgram);

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT);

            glUniform1f(u_time, (float) glfwGetTime());
            glUniform2f(u_mouse, camYaw, camPitch);
            glUniform1f(u_zoom, zoom);
            glUniform2f(u_resolution, 1280f, 720f);

            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void cleanup() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}