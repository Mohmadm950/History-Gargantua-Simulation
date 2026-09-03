<div align="center">

# 🕳️ Gargantua — Black Hole Simulation

### A real-time, physically inspired render of the black hole *Gargantua* from *Interstellar*

<img src="https://readme-typing-svg.demolab.com?font=Fira+Code&size=22&duration=3000&pause=1000&color=F7931E&center=true&vCenter=true&width=600&lines=Real-Time+GLSL+Shader+Rendering;Gravitational+Lensing+Simulation;Volumetric+Accretion+Disk;Built+with+Java+%2B+LWJGL+%2B+OpenGL" alt="Typing SVG" />

<br/>

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![LWJGL](https://img.shields.io/badge/LWJGL-3.x-2E2E2E?style=for-the-badge&logo=opengl&logoColor=white)
![OpenGL](https://img.shields.io/badge/OpenGL-3.3%2B-5586A4?style=for-the-badge&logo=opengl&logoColor=white)
![GLSL](https://img.shields.io/badge/GLSL-Shaders-orange?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Active%20Development-brightgreen?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

<br/>

<!-- Replace this with an actual screen recording or GIF of your render -->
<img src="docs/preview.gif" alt="Gargantua simulation preview" width="720"/>

<sub><i>⚠️ Add your own capture at <code>docs/preview.gif</code> — a screen recording of the accretion disk + lensing in motion sells this project instantly.</i></sub>

</div>

<br/>

## ✨ Overview

This project simulates a **visually realistic black hole** using custom GLSL shaders, rendered in real time on the GPU. Inspired by the depiction of *Gargantua* in Christopher Nolan's *Interstellar*, it focuses on physically-inspired visual effects — gravitational lensing, a glowing volumetric accretion disk, and a distorted starfield — rather than a full general-relativity solver.

The project is **actively evolving**, with ongoing work on performance, stability, and visual fidelity. Some parts of development are accelerated with AI tools, primarily for shader experimentation and optimization.

<br/>

## 🚀 Features

<table>
<tr>
<td width="50%" valign="top">

### 🌌 Rendering
- Shader-based real-time rendering (GLSL)
- Gravitational lensing approximation
- Starfield distortion around the event horizon

</td>
<td width="50%" valign="top">

### 🔥 Effects
- Volumetric accretion disk simulation
- Interactive camera controls (mouse + zoom)
- Continuous visual/performance tuning

</td>
</tr>
</table>

<br/>

## 🎬 Preview

<div align="center">

| Accretion Disk | Gravitational Lensing | Starfield Distortion |
|:---:|:---:|:---:|
| <img src="docs/disk.gif" width="240"/> | <img src="docs/lensing.gif" width="240"/> | <img src="docs/starfield.gif" width="240"/> |

<sub>Drop GIFs or screenshots into a <code>/docs</code> folder and update the paths above — this table is the single biggest thing that will make the repo pop.</sub>

</div>

<br/>

## 🔧 Tech Stack

<div align="center">

![Java](https://img.shields.io/badge/-Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![LWJGL](https://img.shields.io/badge/-LWJGL-2E2E2E?style=flat-square&logo=opengl&logoColor=white)
![GLSL](https://img.shields.io/badge/-GLSL-5586A4?style=flat-square&logo=opengl&logoColor=white)
![Gradle](https://img.shields.io/badge/-Gradle-02303A?style=flat-square&logo=gradle&logoColor=white)

</div>

| Component | Technology |
|---|---|
| Language | Java |
| Graphics bindings | LWJGL (OpenGL) |
| Shaders | Custom GLSL — fragment & vertex |
| Build | Gradle |

<br/>

## ⚙️ Requirements

- ☕ Java 17+
- 🎮 OpenGL 3.3-compatible GPU
- 📦 LWJGL dependencies configured (Gradle recommended)

<br/>

## 🛠️ Getting Started

```bash
# Clone the repository
git clone https://github.com/<your-username>/gargantua-blackhole-sim.git
cd gargantua-blackhole-sim

# Build with Gradle
./gradlew build

# Run
./gradlew run
```

<sub>Adjust the commands above to match your actual Gradle tasks / entry point.</sub>

<br/>

## 🕹️ Controls

| Input | Action |
|---|---|
| 🖱️ Mouse drag | Rotate camera |
| 🖱️ Scroll wheel | Zoom in / out |

<br/>

## 📈 Project Status

> 🚧 **Actively developed — not considered final.**
> Expect frequent updates, optimizations, and visual improvements.

<br/>

## 🗺️ Roadmap

- [ ] Improve lensing accuracy near the photon sphere
- [ ] Add adjustable disk temperature / color gradient
- [ ] Performance profiling on lower-end GPUs
- [ ] Optional VR camera mode

<br/>

## 🤝 Contributing

Contributions, issues, and shader-nerd suggestions are welcome. Feel free to open a PR or start a discussion.

<br/>

## 📄 License

Distributed under the MIT License. See `LICENSE` for details.

<br/>

<div align="center">

**⭐ If you like this project, consider giving it a star — it helps a lot!**

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:0f0c29,100:302b63&height=100&section=footer"/>

</div>
