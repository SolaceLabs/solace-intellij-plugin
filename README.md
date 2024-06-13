# Solace PubSub+ Event Portal Plugin for IntelliJ IDEA

<!-- Plugin description -->
This plugin will connect to the Solace PubSub+ Event Portal pull down various object to display in IntelliJ. 

This repository contains one way of using Event Portal in your Software Development Lifecycle. Check out the [Supercharge your Software Development Lifecycle with PubSub+ Event Portal](https://solace.com/blog/software-development-lifecycle-with-event-portal) blog to learn about other ways you can use Event Portal to throughout your SDLC. 

To use it, you must login to the Solace Event Portal and create a token.  It should have the following permissions:

- All Read permissions for Event Portal
- Get Users (under Account Management)
- View Environments (optional)

## Introduction Video
[![Tour of EP IntelliJ Plugin](https://img.youtube.com/vi/gmoFbjf5pZo/0.jpg)](https://www.youtube.com/watch?v=gmoFbjf5pZo)


This specific section is a source for the [plugin.xml](/src/main/resources/META-INF/plugin.xml) file which will be extracted by the [Gradle](/build.gradle.kts) during the build process.

To keep everything working, do not remove `<!-- ... -->` sections. 
<!-- Plugin description end -->

## Building

1. Clone this repo
2. `./gradlew clean buildPlugin`

The zip file to load into IntelliJ will be located in `build/distributions/`


## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "solace-event-portal</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/SolaceLabs/solace-intellij-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>




