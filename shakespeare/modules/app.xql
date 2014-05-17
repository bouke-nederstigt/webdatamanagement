xquery version "3.0";

module namespace app="http://localhost:8080/exist/apps/shakespear/templates";

import module namespace templates="http://exist-db.org/xquery/templates" ;
import module namespace config="http://localhost:8080/exist/apps/shakespear/config" at "config.xqm";

declare namespace transform="http://exist-db.org/xquery/transform";

(: ~
: Controller to handle loading of items
:)
declare function app:controller($node as node(), $model as map(*), $query as xs:string?, $title as xs:string?, $play as xs:string? ) {
    let $query := lower-case($query)
    let $plays := collection("shakespeare")
    let $title := if(empty($title))
        then false()
        else
            xmldb:decode($title)
    let $play := if(empty($play))
        then false()
        else
            xmldb:decode($play)
  
    return 
        switch ($query)
            case "contents" return                   
                    <div>{app:contents($title)}</div>
            case "act" return                   
                    <div>{app:act($title, $play)}</div>
            case "scene" return                   
                    <div>{app:scene($title, $play)}</div>
            case "character" return                   
                    <div>{app:character($title, $play)}</div>        
          default return
            <ul>{
                let $style := doc("/db/apps/shakespeare/resources/shakes.xsl")            
                for $plays in collection("/apps/shakespeare/collection")    
                order by $plays/PLAY/TITLE/text()
                return 
                    <li><a href="?query=contents&amp;title={$plays/PLAY/TITLE/text()}">{$plays/PLAY/TITLE/text()}</a>{$query}</li>              
            }</ul>    
};

(: ~
 : Function to display contents of a shakespeare play.
 : @param $title The play that needs to be retrieves
 :)

declare function app:contents($title as xs:string) {
    let $style := doc("/db/apps/shakespeare/resources/xslt/content.xsl")
    
    for $play in collection("/apps/shakespeare/collection")
    where $play/PLAY/TITLE/text() = $title
    return
        transform:transform($play, $style, ())
};

(: ~
 : Function to display act
 :)
declare function app:act($title as xs:string, $playTitle as xs:string) {
    let $style := doc("/db/apps/shakespeare/resources/xslt/shakes.xsl")
    
    for $play in collection("/apps/shakespeare/collection")
    where $play/PLAY/TITLE/text() = $playTitle 
    return
       <div>
       {for $act in $play/PLAY/ACT      
       where $act/TITLE/text() = $title
       return
        transform:transform($act, $style, ())
       }
       </div>
};

(: ~
 : Function to display scene
 :)
declare function app:scene($title as xs:string, $playTitle as xs:string) {
    let $style := doc("/db/apps/shakespeare/resources/xslt/shakes.xsl")
    
    for $play in collection("/apps/shakespeare/collection")
    where $play/PLAY/TITLE/text() = $playTitle
    return
       <div>
       {for $scene in $play/PLAY/ACT/SCENE      
       where $scene/TITLE/text() = $title
       return
        transform:transform($scene, $style, ())
       }
       </div>
};

(: ~
 : Function to display act
 :)
declare function app:character($title as xs:string, $play as xs:string) {
    let $style := doc("/db/apps/shakespeare/resources/xslt/shakes.xsl")
    
    for $play in collection("/apps/shakespeare/collection")
    where $play/PLAY/ACT/TITLE/text() = $title
    return 
        transform:transform($play/ACT, $style, ())
};



