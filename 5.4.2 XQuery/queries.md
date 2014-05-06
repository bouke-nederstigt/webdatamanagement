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