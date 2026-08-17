# ft_hangouts

## Introduction

In this project, you will design a mobile application that enables users to create and
manage contacts and send text messages.
The goal is to gain hands-on experience with how mobile applications work, and more
specifically, how the Android operating system interacts with your application. You will
use the Android SDK to develop your application.

### Objectives

You must complete several tasks to understand how an Android app functions.
The goal is to create an app that allows users to create contacts (with at least 5 fields),
edit them, and delete them. Once a contact is saved, users should be able to exchange
text messages.
Contacts must be stored persistently using an SQLite database. Do not use the system’s
shared contact table—create your own. A summary of each contact should be displayed
in a list on the app’s homepage. Users should be able to tap on each contact to view
their details.
Your app must support two different languages, one of them being the default. Changing
the system language should update the app’s language accordingly. When the app is sent
to the background, the timestamp should be saved and displayed in a Toast when the
app returns to the foreground. A menu should allow users to change the header color.
The app icon must be the 42’s logo.

---

## General Instructions

• This project will be evaluated by humans only.
• Feel free to use any language you like.
• No external libraries are allowed (including for UI design)
> You are strongly advised to use Android Studio as your IDE. Note that Google no longer supports the ADT plugin for Eclipse.

---

## Mandatory Part

You must implement the following features:
• Create a contact.
• Edit a contact.
• Delete a contact.
• Homepage displaying a summary for each contact.
• Send text messages to contacts.
• Receive text messages from saved contacts.
• Display a conversation history showing sender and receiver.
• Menu to change the header color.
• Support two different languages.
• Display a Toast with the time the app was last backgrounded.
• App supports both landscape and portrait modes.
• App icon must be the 42’s logo.

---

## Bonus Part

You can implement the following additional features:
• Add a profile picture to each contact.
• Automatically create a new contact if a message is received from an unknown number, using the number as the name.
• Improve the UI using clean Material Design principles.
• Add functionality to call a contact directly from the app.

