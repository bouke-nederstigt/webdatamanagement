<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:exist="http://exist.sourceforge.net/NS/exist" xmlns:shakespeare="http://localhost:8080/exist/apps/shakespeare" version="2.0">
    <xsl:import href="functions.xsl"/>
    <xsl:template match="PLAY">
        <xsl:variable name="playTitle" select="TITLE"/>
        <h3>
            <xsl:value-of select="$playTitle"/>
        </h3>
        <h4>
            <b>Table of Contents</b>
        </h4>
        <ul>
            <xsl:for-each select="ACT">
                <li>
                    <a>
                        <xsl:attribute name="href">?query=act&amp;title=<xsl:value-of select="TITLE"/>&amp;play=<xsl:value-of select="$playTitle"/>
                        </xsl:attribute>
                        <xsl:value-of select="TITLE"/>
                    </a>
                </li>
                <ul>
                    <xsl:for-each select="SCENE">
                        <li>
                            <a>
                                <xsl:attribute name="href">?query=scene&amp;title=<xsl:value-of select="TITLE"/>&amp;play=<xsl:value-of select="$playTitle"/>
                                </xsl:attribute>
                                <xsl:value-of select="TITLE"/>
                            </a>
                        </li>
                    </xsl:for-each>
                </ul>
            </xsl:for-each>
            <li>SPEAKERS</li>
            <xsl:for-each select="PERSONAE">
                <ul>
                    <xsl:for-each select="PERSONA">
                        <li>
                            <a>
                                <xsl:attribute name="href">?query=character&amp;title=<xsl:value-of select="current()"/>&amp;play=<xsl:value-of select="$playTitle"/>
                                </xsl:attribute>
                                <xsl:value-of select="current()"/>
                            </a>
                        </li>
                    </xsl:for-each>
                    <xsl:for-each select="PGROUP">
                        <li>
                            <xsl:value-of select="GRPDESCR"/>
                        </li>
                        <ul>
                            <xsl:for-each select="PERSONA">
                                <li>
                                    <a>
                                        <xsl:attribute name="href">?query=character&amp;title=<xsl:value-of select="current()"/>&amp;play=<xsl:value-of select="$playTitle"/>
                                        </xsl:attribute>
                                        <xsl:value-of select="current()"/>
                                    </a>
                                </li>
                            </xsl:for-each>
                        </ul>
                    </xsl:for-each>
                </ul>
            </xsl:for-each>
        </ul>
    </xsl:template>
</xsl:stylesheet>