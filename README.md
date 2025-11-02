
<div align="center">
  <img height="333" src="https://github.com/Hoisasa/English-word-learning/blob/readme/assets/images/sharkonamiTransparent.png?raw=true">
</div>
<h1 align="center"> Sharks' Empire: English Android</h1>


<p align=center>
<img alt="Static Badge" src="https://img.shields.io/badge/Kotlin-2.1.21-%237F52FF?logo=kotlin&logoColor=%23EFEFEF">
<img alt="Static Badge" src="https://img.shields.io/badge/Jetpack%20Compose-DB-%234285F4?logo=jetpackcompose&logoColor=%23EFEFEF">
<img alt="Static Badge" src="https://img.shields.io/badge/Kokoro-TTS-ebb434">
<img alt="Static Badge" src="https://img.shields.io/badge/StyleTTS2-TTS-cc8a33">
<img alt="Static Badge" src="https://img.shields.io/badge/SQLite-DB-%23003B57?logo=sqlite&logoColor=%23EFEFEF">
<img alt="Static Badge" src="https://img.shields.io/badge/English Level-B2-ba5df0">
<img alt="Static Badge" src="https://img.shields.io/badge/Licence-GPL3.0-green">
</p>




<dl align=center>
  <dt>An English Learning app for windows in a form of flashcards.</dt>
  <dd>Provides words upto B2 level of English without the need for subscription</dd>
</dl>

<!-- ## Project timeline -->
  &nbsp;
<p align=center>
  <img src="https://github.com/user-attachments/assets/8c1f9ad7-ee55-414f-80a6-6afe3edd93ef">
</p>

## Table of contents

-  [Screenshots](#camera-Screenshots)
-  [Key Features](#Key-Features)
-  [Installation](#Installation)
-  [Tweaking project](#How-to-tweak-project-for-your-own-needs)
-  [Credits](#Credits)

## :camera: Screenshots
<p align=center>
  <img width="300" height="480" alt="Image" src="https://github.com/user-attachments/assets/708af005-9dcc-43f0-863a-10859dec5f84" />
  <img width="300" height="480" alt="Image" src="https://github.com/user-attachments/assets/0258c158-cc9b-4d2c-ad46-f1fec1c626fa" />
  <img width="300" height="480" alt="Image" src="https://github.com/user-attachments/assets/af137cde-6555-4dbd-9c21-774ecef17576" />
</p>
<details>
<summary align="center">
  <span style="color:blue; font-weight:bold;">Show more screenshots</span>
</summary>
<p align=center style="overflow-x:auto; white-space:nowrap; border:1px solid #ccc; padding:5px;">
<img width="353" height="220" alt="Image" src="https://github.com/user-attachments/assets/8909e0a5-6241-4d0c-8960-a0708b184c55" />
<img width="353" height="220" alt="Image" src="https://github.com/user-attachments/assets/cda5121e-394c-4588-9351-1bbd87bf9cb4" />
<img width="353" height="220" alt="Image" src="https://github.com/user-attachments/assets/66151a11-65d9-4bbe-aae3-5d92a5923d98" />
<img width="353" height="220" alt="Image" src="https://github.com/user-attachments/assets/4e85d2d6-d9a6-46cf-a403-58833056542f" />
</p>
</details>


## Key Features

There are three modes Overview Practice and Exam.
While overview just straight up shows the translation, others provide it after pressing a designated button.
In the end, all mistakes are shown as so to provide you with a feature to reflect on your answers.
To gauge the progress of learning each word, they have points assigned to them.
When you are ready - complete the exam: a more strict area which punishes mistakes more.
The exam goal is to make as few mistakes as possible
But in order to not be utterly strict it allows one mistake to be completed

✅**The features of an app are as follows:**
- 1️⃣**Only one part of speech per group, less confusion**
- 📦**Groups consist of subgroups**
- 🧮**Mostly just 10 words per subgroup** flashcards organised into quite small groups for easier memorization.
- 🎯**self evaluation** We don't put a goal of checking your typing speed or pronunciation, thus it's your responsibility to evaluate the answer. We also wanted to reduce guessing factor which `four options` introduces so if you see that your translation didn't align with given one - press a `wrong answer` button
- 🌊👋.**context-sensitive translations** it aims to show beginner how words meanings can vary depending on context
- 🗣️**target learner language Russian**
> [!Hint]
> (**you can provide your own translations for db**) [Example](#add-your-vocabulary-or-locale)

## Credits

This software uses the following packages and resources:

* uses Kokoro tts from [This repository](https://github.com/hexgrad/kokoro)

```mermaid
mindmap
root((Learned Stuff))
  MVVM
    View
      ViewModel
        Business logic
        flow
          internal private
          exteranl public
            return@launch
            return@update
            collect 
              as state
              as state with lifecycle
        saved state handle
      Repository            
        ROOM 
          Entities
          Daos
          Singleton pattern
            Dependency Injection
              Hilt
          version migrations
  Kotlin
    type safety operators
      non null !!
      safe-call ?.
    objects
    classes
      sealed
        getting list of all
      data
      interfaces
    functions
      private
      inline
      suspend
        class::function reference
  Testing
    Unit Tests
    Espresso
    app inspection
      SQL requests test
    logcat debug messages
  Jetpack Compose
    Navhost
    states
      mutable
      remember
        savable
          custom saver
    recompositions
      Screen rotation issue
      Launched effects
    Constraint Layouts
      references
      Constraint sets
      guidelines
  SOLID
  Gradle
    dependencies
      libs.toml
      chaquopy
        building python dep

```


