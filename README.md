# FEM Body Deformer

---

## About the Project

This application is a cross-platform tool for 3D modeling and analyzing the stress-strain state of spatial structures using the Finite Element Method (FEM). The program generates a mesh of 20-node isoparametric finite elements, allows interactive application of boundary conditions (fixations and pressure), and calculates the displacement vector, stress tensor, and principal stresses of the material.
The project is developed using Kotlin Multiplatform, allowing it to run on Android, iOS, and Desktop (JVM) using a single codebase for business logic and UI.

## Core Features

- **Dynamic Mesh Generation:** Constructs a 3D mesh for a rectangular parallelepiped with user-defined dimensions and subdivision counts along the X, Y, and Z axes.
- **Interactive 3D Interface:** A custom 3D engine built on Jetpack Compose for rotating, scaling the camera, and selecting external faces of the model using ray-casting and Z-buffering.
- **High-Performance Computing Core:**
  -	Gaussian numerical integration (3 points).
  -	Analytical inversion of the ‭$3 \times 3$‬‭‬ Jacobian matrix using Cramer's rule.
  -	Assembly of a global band stiffness matrix for RAM optimization.
  -	Solving the global System of Linear Algebraic Equations (SLAE) using the iterative Conjugate Gradient Method.
- **Results Analysis:** Visualization of the deformed state of the structure (a red wireframe rendered over the initial gray one) and a detailed text verification report containing numerical values of matrices, displacements, and stresses.

## Tech Stack

- **Language:** Kotlin 2.4.20
- **Architecture:** Clean Architecture + MVVM (Model-View-ViewModel)
- **DI:** Koin 4.2.2
- **UI:** Compose Multiplatform (Jetpack Compose)
- **3D Engine:** Custom Vector3 class & custom SimpleCamera class to project the FEM body, its faces, and nodes.
- **Concurrency:** Kotlin Coroutines (Dispatchers.Default for SLAE calculation parallelization).

## Project Structure

This is a Kotlin Multiplatform project targeting Android, iOS, Desktop (JVM).
- /iosApp contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform, you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.
- /shared is for code that will be shared across your Compose Multiplatform applications. It contains several subfolders:
- commonMain is for code that’s common for all targets.
- iosMain and jvmMain are for platform-specific API calls and initializations (e.g., Koin startup configuration).

Detailed commonMain Structure:

```text
.
├── di/                       # Koin DI Module (dependencies for FEMBody and FEMViewModel)
├── domain/                   # FEM-Body related models and business logic
│   ├── FEMBody.kt            # FEM mathematical core (matrices, SLAE assembly, solver)
│   └── GaussConstants.kt     # Singleton with cached Gauss nodes and weight coefficients
├── ui/                       # UI
│   ├── models/               # Models of 3D Engine (SimpleCamera, Vector3)
│   ├── scene/                # Application orchestration
│   │   ├── components/       # Components of the scene (control panel, info panel, body drawing etc.)
│   │   └── Scene.kt          # Main Composable screen with 3D-Canvas and settings
│   ├── utilities/            # Defined colors and CustomText component
│   ├── viewmodels/           # FEMViewModel (UI state management, background calculation calls)
│   └── App.kt                # Main file where Scene function is called
```

## Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:
- Android app:
 ```bash
./gradlew :androidApp:assembleDebug
```
- Desktop app:
  - Hot reload: 
```bash
  ./gradlew :desktopApp:hotRun --auto
```
  - Standard run: 
```bash
 ./gradlew :desktopApp:run
```
- iOS app: open the /iosApp directory in Xcode and run it from there.