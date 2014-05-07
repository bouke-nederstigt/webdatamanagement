1. **Title:** /movies//title
2. **All movie titles:** /movies//title/text()
3. **Title of the movies published after 2000**: /movies/movie[year>2000]/title/text()
4. **Summary of "Spider-man"** /movies/movie[title="Spider-Man"]/summary/text()
5. **Who is the director of Heat?** concat(/movies/movie[title='Heat']/director/first_name/text(), ' ', /movies/movie[title='Heat']/director/last_name/text())
6. **Title of the movies featuring Kirsten Dunst** /movies/movie[actor/first_name="Kirsten" and actor/last_name="Dunst"]/title/text()
7. **Which movies have a summary?** /movies/movie[summary and string-length(summary/text()) != 0]/title/text()
8. **Which movies do not have a summary?** /movies/movie[not(summary) or (string-length(summary/text()) = 0)]/title/text()
9. **Titles of movies published more than 5 years ago** /movies/movie[year <= 'year-from-date(current-date())-5']/title/text()
10. **What was the role of Clint Eastwoord in Unforgiven?** /movies/movie[title="Unforgiven"]/actor[first_name="Clint" and last_name="Eastwood"]/role/text()
11. **What is the last movie of the document?**/movies/movie[last()]
12. **Title of the film that immediatly precedes Marie Antoinette in the document?** /movies/movie[title="Marie Antoinette"]/preceding-sibling::*[1]/title/text()
13. **Get the movies whose title containts a "V"** /movies/movie[contains(title, "V")]/title/text()
14. **Get the movies whose cast consist of exactly three actors** /movies/movie[count(actor) = 3]/title/text()
