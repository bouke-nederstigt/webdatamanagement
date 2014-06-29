1. **List the movies published after 2002, including their title and year**
    `<results>{
let $ms := doc("movies/movies_alone.xml"),
$as := doc("movies/artists_alone.xml")
for $a in $ms//movie
where $a/year > 2002
return 
    <result>
       {$a/title}
        {$a/year}
    </result>}
</results>`
2. **Create a flat list of all title-role pairs, enclosed in a "result element"**
    `<results>{
let $ms := doc("movies/movies_alone.xml"),
$as := doc("movies/artists_alone.xml")
for $t in $ms//movie
return 
    for $a in $t//actor
    return 
        <result>
            {$t/title}
            <role>{string($a/@role)}</role>
        </result>
}
</results>`
3. **Give the title of movies where the director is also one of the actors**
    `let $ms := doc("movies/movies_alone.xml"),
$as := doc("movies/artists_alone.xml")
for $t in $ms//movie
where $t/director/@id = $t/actor/@id 
return $t/title/text()`
4. **Show the movies, grouped by genre**
    `<results>{
let $ms := doc("movies/movies_alone.xml"),
$as := doc("movies/artists_alone.xml")
for $genre in distinct-values($ms//movie/genre)
let $t := $ms//movie[genre=$genre]
return 
    <result>
        <genre>
            {$genre}
        </genre>
            {$t}
    </result>
}</results>`
5. **For each distinct actor's id, show the titles of the movies where this actor plays a role.**
    `<results>{
let $ms := doc("movies/movies_alone.xml"),
$as := doc("movies/artists_alone.xml")
for $actorId in distinct-values($ms//movie/actor/@id)
let $t := $ms//movie[actor/@id = $actorId]/title
return 
    <actor>
        {$actorId},{$t}
    </actor>
}</results>`
*Variant only showing actors playing in at least two movies:* `<results>{
let $ms := doc("movies/movies_alone.xml"),
$as := doc("movies/artists_alone.xml")
for $actorId in distinct-values($ms//movie/actor/@id)
let $t := $ms//movie[actor/@id = $actorId]/title
return 
    if(count($t) > 1) then
        <actor>
            {$actorId},{$t}
        </actor>
    else
        ()
}</results>`
6. **Give the title of each movie, along with the name if its director**
    `<results>{
let $ms := doc("movies/movies_alone.xml"),
$as := doc("movies/artists_alone.xml")
for $movie in $ms//movie, $director in $as//artist[@id = $movie/director/@id]
return 
    <result>
        {$movie/title}
        <director>
            {$director/first_name}
            {$director/last_name}
        </director>
    </result>
}</results>`
7. **Give the title of each movie, and a nested element <actors> giving the list of actors with their role**
    `<results>{
let $ms := doc("movies/movies_alone.xml"),
$as := doc("movies/artists_alone.xml")
for $movie in $ms//movie 
return 
    <result>
        {$movie/title} 
        <actors>
    {for $a in $movie/actor, $actor in $as//artist[@id = $a/@id]
    return
       <actor>
           {$actor/first_name}
           {$actor/last_name}
           <role>{string($a/@role)}</role>
       </actor> 
    }
        </actors>
    </result>
}</results>`
8. **For each movie that has at least two actors, list the title and firs two actors, and an empty "et-al"element if the movie has additional actors.**
    `<results>{
let $ms := doc("movies/movies_alone.xml"),
$as := doc("movies/artists_alone.xml")
for $movie in $ms//movie 
let $count := count($movie/actor)
return
    if($count > 1) then
        <result>
            {$movie/title} 
        {for $a in subsequence($movie/actor, 1, 2), $actor in $as//artist[@id = $a/@id]
        return        
               <actor>{$actor/first_name/text()} {$actor/last_name/text()} as {string($a/@role)} </actor>            
        }
        {if($count > 2) then
            <et-al/>
        else
            ()
        }
        </result>
    else
        ()
}</results>`
9. **List the titles and years of all movies directed by Clint Eastwood after 1990, in alphabetical order**
    `<results>{
let $ms := doc("movies/movies_alone.xml"),
$as := doc("movies/artists_alone.xml")
for $actor in $as//artist[first_name="Clint" and last_name="Eastwood"]
    for $movie in $ms//movie[director/@id = $actor//@id]
    where $movie/year > 1990
    order by $movie/title
    return 
        <result>
            {$movie/title}
            {$movie/year}
        </result>
}</results>`
