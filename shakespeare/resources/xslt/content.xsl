<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:exist="http://exist.sourceforge.net/NS/exist" version="1.0">
    <xsl:template match="PLAY">
        <p>
            <b>Table of Contents</b>
        </p>
        <ul>
            <xsl:for-each select="PERSONAE|ACT">
                <li>
                    <a href="#{generate-id()}">
                        <xsl:value-of select="TITLE"/>
                    </a>
                </li>
                <ul>
                    <xsl:for-each select="SCENE">
                        <li>
                            <a href="#{generate-id()}">
                                <xsl:value-of select="TITLE"/>
                            </a>
                        </li>
                    </xsl:for-each>
                </ul>
            </xsl:for-each>
        </ul>
    </xsl:template>
</xsl:stylesheet>