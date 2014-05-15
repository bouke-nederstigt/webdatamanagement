xquery version "3.0";

module namespace app="http://exist/apps/movie-lookup/templates";
import module namespace templates="http://exist-db.org/xquery/templates" at "templates.xql";
import module namespace config="http://exist/apps/movie-lookup/config" at "config.xqm";
declare namespace transform="http://exist-db.org/xquery/transform";

(:
 :  Retrieve the list of movies based on the filter criteria
 :)
declare function app:submit($node as node(), $model as map(*), $query as xs:string?, $querytype as xs:string?) {
    (: If they don't enter anything, we throw an error prompt. :)
    let $query := lower-case($query)
    let $movie_list := collection("apps/movie-lookup/movies")/movies/movie
    return
    if (empty($query)) then (
        <div class="container">
            <h2>Please enter a search criteria!</h2>
        </div>
    )
    else if ($querytype = "title") then (
        let $title := lower-case(normalize-space($query))
        for $movie in $movie_list[contains(lower-case(title/text()), $title)]
        return
            <div>{app:display($movie)}</div>
    )
    else if ($querytype = "keywords") then (
        for $movie in $movie_list
        return
            <div>{app:display($movie)}</div>
    )
    else if ($querytype = "year") then (
        let $year := lower-case(normalize-space($query))
        for $movie in $movie_list[year/text() = $year]
        return
            <div>{app:display($movie)}</div>
    )
    else if ($querytype = "director") then (
        let $director_fname := lower-case(substring-before(normalize-space($query), ' '))
        let $director_lname := lower-case(substring-after(normalize-space($query), ' '))
        for $movie in $movie_list[(lower-case(director/first_name/text()) = $director_fname) and (lower-case(director/last_name/text()) = $director_lname)]     
        return
            <div>{app:display($movie)}</div>
    )
    else if ($querytype = "actor") then (
        let $actor_fname := lower-case(substring-before(normalize-space($query), ' '))
        let $actor_lname := lower-case(substring-after(normalize-space($query), ' '))
        for $movie in $movie_list
            for $actor in $movie/actor
            where (lower-case($actor/first_name/text()) = $actor_fname) and (lower-case($actor/last_name/text()) = $actor_lname)      
            return
                <div>{app:display($movie)}</div>
    )
    else if ($querytype = "genre") then (
        let $genre := lower-case(normalize-space($query))
        for $movie in $movie_list[contains(lower-case(genre/text()), $genre)]
        return
            <div>{app:display($movie)}</div>
    )
    else
        ()
};

declare function app:display($movies as node()) {
    <div class="table-responsive">
      <table class="table table-hover">
        <tr>
            <td>{$movies/title/text()}</td>
            <td>{$movies/year/text()}</td>
        </tr>
      </table>
    </div>    
};