<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:shakespeare="http://localhost:8080/exist/apps/shakespeare" exclude-result-prefixes="xs" version="2.0">
    <xsl:output method="html" version="4.0" encoding="UTF-8" indent="yes"/>
    
    <!-- Function to create url based on action (character, act, scene etc., title of target, play -->
    <xsl:function name="shakespeare:createLink" as="element()">
        <xsl:param name="action"/>
        <xsl:param name="title"/>
        <xsl:param name="play"/>
        <xsl:variable name="href" select="query=$action"/>
        <a href=""/>
    </xsl:function>
</xsl:stylesheet>