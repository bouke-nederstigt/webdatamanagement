xquery version "3.0";

module namespace app="http://exist/apps/movie-lookup/templates";
import module namespace templates="http://exist-db.org/xquery/templates" at "templates.xql";
import module namespace config="http://exist/apps/movie-lookup/config" at "config.xqm";
declare namespace transform="http://exist-db.org/xquery/transform";

(:~
 :  Retrieve the list of movies based on the filter criteria
 :)
declare function app:submit($node as node(), $model as map(*), $title as xs:string?, $genre as xs:string?, $director as xs:string?, $actor as xs:string?, $year as xs:string?, $keywords as xs:string?) {
    (: If they don't enter anything, we throw an error prompt. :)
    if (not($title) and not($genre) and not($director) and not($actor) and not($actor) and not($year) and not($keywords))
    then (
        <div class="container"><h2>Please enter a search criteria</h2></div>
    )
    else 
    (
        let $year := lower-case($year)
        let $title := lower-case(normalize-space($title))
        let $genre := lower-case($genre)
        let $director_fname := lower-case(substring-before(normalize-space($director), ' '))
        let $actor_fname := lower-case(substring-before(normalize-space($actor), ' '))
        let $director_lname := lower-case(substring-after(normalize-space($director), ' '))
        let $actor_lname := lower-case(substring-after(normalize-space($actor), ' '))
        let $keywords := lower-case(normalize-space($keywords))        
                
        for $movie in collection("apps/movie-lookup/movies")/movies/movie[contains(lower-case(title/text()), $title) and contains(lower-case(summary/text()), $keywords) and contains(lower-case(genre/text()), $genre)]
        where ($director and ((lower-case($movie/director/first_name/text()) = $director_fname) and (lower-case($movie/director/last_name/text()) = $director_lname)))
        return
            $movie/title/text()
    )
};