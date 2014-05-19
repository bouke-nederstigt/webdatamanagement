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
    let $plays := collection("/apps/shakespeare/collection")
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
                    <li><a href="?query=contents&amp;title={$plays/PLAY/TITLE/text()}&amp;play={$plays/PLAY/TITLE}">{$plays/PLAY/TITLE/text()}</a>{$query}</li>              
            }</ul>    
};

(:
:Display title of current play in menu
:)
declare function app:title($node as node(), $model as map(*), $play as xs:string?){
    let $play := if(empty($play))
        then 'false'
        else
            xmldb:decode($play)
    
    return
        if($play = 'false')
        then
            <a href="./index.html" class="brand">Shakespeare</a>
        else
            <a href="?query=contents&amp;title={$play}&amp;play={$play}" class="brand">{$play}</a>
};

(: 
:Generate menu plays 
:)
declare function app:menu_plays($node as node(), $model as map(*), $play as xs:string? ){ 
    let $play := if(empty($play))
        then false()
        else
            xmldb:decode($play)
    return      
        <ul class="dropdown-menu">{
            for $plays in collection("/apps/shakespeare/collection") 
            order by $plays/PLAY/TITLE/text()
            return
                <li><a href="?query=contents&amp;title={$plays/PLAY/TITLE}&amp;play={$plays/PLAY/TITLE}">{$plays/PLAY/TITLE/text()}</a></li>
        }</ul>
};

(: 
:Generate menu acts 
:)
declare function app:menu_acts($node as node(), $model as map(*), $play as xs:string? ){ 
    let $play := if(empty($play))
        then 'false'
        else
            xmldb:decode($play)
    return         
        if($play = 'false') then
            <ul class="dropdown-menu">Please select a play</ul>
        else
            <ul class="dropdown-menu">{
                for $plays in collection("/apps/shakespeare/collection")
                where $plays/PLAY/TITLE/text() = $play
                return
                    for $acts in $plays/PLAY/ACT
                    return
                        <li><a href="?query=act&amp;title={$acts/TITLE}&amp;play={$play}&amp;act={$acts/TITLE}">{$acts/TITLE/text()}</a></li>
            }</ul>
};

(: 
:Generate menu scenes 
:)
declare function app:menu_scenes($node as node(), $model as map(*), $play as xs:string?, $act as xs:string? ){ 
    let $play := if(empty($play))
        then 'false'
        else
            xmldb:decode($play)
            
    let $act := if(empty($act))
        then 'false'
        else
            xmldb:decode($act)
    return         
        if($play = 'false' or $act = 'false') then
            <ul class="dropdown-menu">Please select a play and act</ul>
        else
            <ul class="dropdown-menu">{
                for $plays in collection("/apps/shakespeare/collection")
                where $plays/PLAY/TITLE/text() = $play 
                return
                    for $acts in $plays/PLAY/ACT
                    where $acts/TITLE/text() = $act
                    return
                        for $scenes in $acts/SCENE
                        return
                            <li><a href="?query=scene&amp;title={$scenes/TITLE}&amp;play={$play}&amp;act={$act}">{$scenes/TITLE/text()}</a></li>
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
        transform:transform($act, $style, (<parameters><param name="playTitle" value="{$playTitle}"/></parameters>))
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
        transform:transform($scene, $style, (<parameters><param name="playTitle" value="{$playTitle}"/></parameters>))
       }
       </div>
};

(: ~
 : Function to display character
 :)
declare function app:character($title as xs:string, $playTitle as xs:string) {
    let $style := doc("/db/apps/shakespeare/resources/xslt/shakes.xsl")
    let $actualTitle := if(string-length(fn:substring-before($title, ',')) = 0)
                        then $title
                        else fn:substring-before($title, ',')
    let $actualTitle := fn:normalize-space($actualTitle)
    for $play in collection("/apps/shakespeare/collection")
    where $play/PLAY/TITLE/text() = $playTitle
    return 
        <div class="panel-group" id="accordion">
         {
            for $scene in $play/PLAY/ACT/SCENE
            return
                    for $character in $scene/SPEECH
                    where $character/SPEAKER/text() = $actualTitle    
                    return
                        transform:transform($character, $style, (
                        <parameters>
                            <param name="playTitle" value="{$playTitle}"/>
                            <param name="characterPage" value="true" />                  
                        </parameters>))
                  
         }
        </div>
};



