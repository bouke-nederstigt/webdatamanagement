<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:template match="/">
        <html>
            <head>
        </head>
            <body>
                <div class="table-condensed">
                    <table class="table table-hover table-striped">
                        <tr>
                            <th class="col-md-8">Movie</th>
                            <th class="col-md-2">Year</th>
                        </tr>
                        <xsl:for-each select="movie">
                            <xsl:variable name="summaryId" select="generate-id()"/>
                            <tr>
                                <td>
                                    <a href="#" data-toggle="modal" class="summaryInfo" data-id="">
                                        <xsl:attribute name="data-target">#<xsl:value-of select="$summaryId"/>
                                        </xsl:attribute>
                                        <xsl:value-of select="title"/>
                                    </a>
                                </td>
                                <td>
                                    <xsl:value-of select="year"/>
                                </td>
                            </tr>
                            <div class="modal fade" id="summaryModal" tabindex="-1" role="dialog" aria-labelledby="summaryModal" aria-hidden="true">
                                <xsl:attribute name="id">
                                    <xsl:value-of select="$summaryId"/>
                                </xsl:attribute>
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
                                            <h2 class="modal-title" id="summaryModalLabel">
                                                <xsl:value-of select="title"/> (<xsl:value-of select="year"/>)<br/>
                                            </h2>
                                        </div>
                                        <div class="modal-body" id="{generate-id()}">
                                            <h4>Genre: <xsl:value-of select="genre"/>
                                            </h4>
                                            <h4>Plot:</h4>
                                            <p>
                                                <xsl:value-of select="summary"/>
                                            </p>
                                            <h4>Director: <xsl:value-of select="director/first_name"/>&#160;<xsl:value-of select="director/last_name"/>
                                            </h4>
                                            <h4>Cast:</h4>
                                            <ul>
                                                <xsl:for-each select="actor">
                                                    <li>
                                                        <xsl:value-of select="first_name"/>&#160;<xsl:value-of select="last_name"/> as "<xsl:value-of select="role"/>"</li>
                                                </xsl:for-each>
                                            </ul>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </xsl:for-each>
                    </table>
                </div>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>