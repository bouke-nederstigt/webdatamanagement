# Shakespeare application Architecture description #

The purpose of the application is to be able to browse through a Shakespeare play in order to analyse it's content, read some specific parts and maybe find related information.

To do so the application will consist of the following components:

- Pages	
 - Overview of all plays, linking to contents of play
	- Contents of play, includes organization in acts and scenes and characters present in scene
	- Displaying of character part, act and scene
- Navigation handler. Decides which page to return based on url.
- All scenes and character references appearing should link to their respective pages
- Menu for navigation
	- Should always include link to full summary of current  play
	- Link to overview of plays

	