xquery version "3.0";

module namespace app="http://localhost:8080/exist/apps/shakespear/templates";

import module namespace templates="http://exist-db.org/xquery/templates" ;
import module namespace config="http://localhost:8080/exist/apps/shakespear/config" at "config.xqm";

declare namespace transform="http://exist-db.org/xquery/transform";

(:~
 : This is a sample templating function. It will be called by the templating module if
 : it encounters an HTML element with an attribute dathttp://localhost:8080/exist/apps/eXide/index.html#a-template="app:test" 
 : or class="app:test" (deprecated). The function has to take at least 2 default
 : parameters. Additional parameters will be mapped to matching request or session parameters.
 : 
 : @param $node the HTML node with the attribute which triggered this call
 : @param $model a map containing arbitrary data - used to pass information between template calls
 :)
declare function app:test($node as node(), $model as map(*)) {
    <p>Dummy template output generated by function app:test at {current-dateTime()}. The templating
        function was triggered by the class attribute <code>class="app:test"</code>.</p>
};

declare function app:controller($node as node(), $model as map(*), $query as xs:string?, $title as xs:string? ) {
    let $query := lower-case($query)
    let $plays := collection("shakespeare")
    let $title := if(empty($title))
        then false()
        else
            xmldb:decode($title)
  
    return 
        switch ($query)
            case "content" return                   
                    <div>{app:contents($title)}</div>
          
          default return
            <ul>{
                let $style := doc("/db/apps/shakespeare/resources/shakes.xsl")            
                for $plays in collection("shakespeare")    
                order by $plays/PLAY/TITLE/text()
                return 
                    <li><a href="?query=content&amp;title={$plays/PLAY/TITLE/text()}">{$plays/PLAY/TITLE/text()}</a>{$query}</li>              
            }</ul>    
};

(: ~
 : Function to display contents of a shakespeare play.
 : @param $title The play that needs to be retrieves
 :)

declare function app:contents($title as xs:string) {
    let $style := doc("/db/apps/shakespeare/resources/xslt/content.xsl")
    
    for $play in collection("shakespeare")
    where $play/PLAY/TITLE/text() = $title
    return
        transform:transform($play, $style, ())
};

