# QuestFlow

## Showcase
https://aryamanarora.com/CalendarApp/

## Goal
Develop a full-stack application that assists Waterloo students with course planning for upcoming semesters by preventing and detecting time conflicts, providing a calendar view of their schedule, along with functionality to share schedules with peers/friends. 

## Team Members
Aryaman Arora, <a96arora@uwaterloo.ca> <br>
Aryaman Dhillon, <a38dhill@uwaterloo.ca> <br>
Cedric Wang, <c689wang@uwaterloo.ca> <br>
Sachi Shah, <s274shah@uwaterloo.ca> <br>

## Project Proposal
https://github.com/aarora20/CalendarApp/wiki/Project-Proposal

## Getting Started
Install the client for your system in [releases](https://github.com/aarora20/CalendarApp/tree/main/releases) either [Final_Release_Quest_Flow.dmg](https://github.com/aarora20/CalendarApp/blob/main/releases/Final_Release_QuestFlow.dmg) or [Final_Release_Quest_Flow.msi](https://github.com/aarora20/CalendarApp/blob/main/releases/Final_Release_QuestFlow.dmg)

## Detailed User Guide
https://github.com/aarora20/CalendarApp/wiki/User-Guide

## Instructions for Running and Building
https://github.com/aarora20/CalendarApp/wiki/Instructions

## Architecture & Design
https://github.com/aarora20/CalendarApp/wiki/Architecture

## Reflections on Practices
https://github.com/aarora20/CalendarApp/wiki/Reflection

## Software Releases
https://github.com/aarora20/CalendarApp/wiki/Releases

## Sources
The following links are sources that we used to build our project: <br>

Calendar Layout: <https://danielrampelt.com/blog/jetpack-compose-custom-schedule-layout-part-1/>
   - The logic for implementing the calendar layout including drawing each course to the right time. <br>

Fuzzy String Searching: <https://github.com/willowtreeapps/fuzzywuzzy-kotlin>
   - Originally, we were trying use this github library as an implementation dependency, but it turns out that the library was no longer maintained and the importing it was not an option. As such, we had to fork their repository to our own. <br>

Drag and Drop: <https://blog.canopas.com/android-drag-and-drop-ui-element-in-jetpack-compose-14922073b3f1>
   - The blog was used to help with implementing the drag and drop feature for our custom calendars. <br>

OptaPlanner Logic: <https://stackoverflow.com/questions/71572097/is-optaplanner-scheduling-ai-suitable-for-optimising-a-college-schedule>
   - The StackOverflow post was used to help use implementing the constraints for our course schedule optimization where the hard constraint was no overlapping courses, the medium constraint was minimize working days and the soft constraint was minimizing gaps between courses on the same day. <br>

## Support
For any inquiries, please email <calendarappgcp@gmail.com> or any of the team members.
