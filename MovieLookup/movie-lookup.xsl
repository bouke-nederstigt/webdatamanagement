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
                            <tr>
                                <td>
                                    <a href="#" data-toggle="modal" class="summaryInfo" data-id="" data-target="#summaryModal">
                                        <xsl:value-of select="title"/>
                                    </a>
                                </td>
                                <td>
                                    <xsl:value-of select="year"/>
                                </td>
                            </tr>
                            <div class="modal fade" id="summaryModal" tabindex="-1" role="dialog" aria-labelledby="summaryModal" aria-hidden="true">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
                                            <h4 class="modal-title" id="summaryModalLabel">Summary</h4>
                                        </div>
                                        <div class="modal-body">
                                            <h3>
                                                <xsl:value-of select="summary"/>
                                            </h3>
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