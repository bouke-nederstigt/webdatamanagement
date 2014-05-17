xquery version "3.0";

module namespace app="http://exist/apps/movie-lookup/templates";
import module namespace templates="http://exist-db.org/xquery/templates" at "templates.xql";
import module namespace config="http://exist/apps/movie-lookup/config" at "config.xqm";
declare namespace transform ="http://exist-db.org/xquery/transform";

 (:
 :  Retrieve the list of movies based on the filter criteria
 :)
declare function app:submit($node as node(), $model as map(*), $query as xs:string?, $querytype as xs:string?) {
    (: If they don't enter anything, we throw an error prompt. :)
    let $query := lower-case($query)
    let $movie_list := collection("apps/movie-lookup/movies")/movies/movie
    return
        if ($query = "") then 
        (
            <div>{app:displayEmpty('Please enter your search string.')}</div>
        )
        else 
        (
            switch ($querytype)
            case "title" return 
                let $title := lower-case(normalize-space($query))
                for $movie in $movie_list[contains(lower-case(title/text()), $title)]
                return
                    <div>{app:display($movie)}</div>    
            
            case "keywords" return 
                let $keywords := lower-case(normalize-space($query))
                for $movie in $movie_list[contains(lower-case(summary/text()), $keywords)]
                return
                    <div>{app:display($movie)}</div> 
                        
            case "year" return 
                let $year := lower-case(normalize-space($query))
                for $movie in $movie_list[year/text() = $year]
                return
                    <div>{app:display($movie)}</div>
                        
            case "director" return 
                let $director_fname := lower-case(substring-before(normalize-space($query), ' '))
                let $director_lname := lower-case(substring-after(normalize-space($query), ' '))
                for $movie in $movie_list[(lower-case(director/first_name/text()) = $director_fname) and (lower-case(director/last_name/text()) = $director_lname)]     
                return
                    <div>{app:display($movie)}</div> 
                        
            case "actor" return 
                let $actor_fname := lower-case(substring-before(normalize-space($query), ' '))
                let $actor_lname := lower-case(substring-after(normalize-space($query), ' '))
                for $movie in $movie_list
                    for $actor in $movie/actor
                    where (lower-case($actor/first_name/text()) = $actor_fname) and (lower-case($actor/last_name/text()) = $actor_lname)      
                    return
                        <div>{app:display($movie)}</div>  
                        
            case "genre" return 
                let $genre := lower-case(normalize-space($query))
                for $movie in $movie_list[contains(lower-case(genre/text()), $genre)]
                return
                    <div>{app:display($movie)}</div> 
                        
            default return
                let $title := lower-case(normalize-space($query))
                let $keywords := $title
                let $year := $title
                let $genre := $title
                let $director_fname := lower-case(substring-before(normalize-space($query), ' '))
                let $director_lname := lower-case(substring-after(normalize-space($query), ' '))
                for $movie in $movie_list[(contains(lower-case(title/text()), $title)) or (contains(lower-case(summary/text()), $keywords)) or (year/text() = $year) or ((lower-case(director/first_name/text()) = $director_fname) and (lower-case(director/last_name/text()) = $director_lname)) or (contains(lower-case(genre/text()), $genre))]
                    return
                        <div>{
                            if ($movie) then
                                app:display($movie)
                            else 
                            (
                                for $movie in $movie_list
                                    let $actor_fname := $director_fname
                                    let $actor_lname := $director_lname
                                    for $actor in $movie/actor
                                    where (lower-case($actor/first_name/text()) = $actor_fname) and (lower-case($actor/last_name/text()) = $actor_lname)
                                    return
                                        <div>{
                                            if ($movie) then
                                                app:display($movie)
                                            else
                                                <div>{app:displayEmpty('No results found.')}</div>
                                        }</div>     
                            )
                        }</div>  
        )
};

declare function app:displayEmpty($displayString as xs:string?) {
    <div class="container">{$displayString}</div>
};

declare function app:display($movies as node()) {
    let $xsl := doc("/db/apps/movie-lookup/movie-lookup.xsl")
    return
        transform:transform($movies, $xsl, ())
};
