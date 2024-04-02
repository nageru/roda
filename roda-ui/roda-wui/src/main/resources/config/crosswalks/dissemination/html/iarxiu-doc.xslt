<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:voc="http://schemas.user.iarxiu.hp.com/2.0/Voc_document"
                xmlns="http://schemas.user.iarxiu.hp.com/2.0/Voc_document"                
                exclude-result-prefixes="voc">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"
                omit-xml-declaration="yes" />
    <xsl:param name="i18n.title"/>
    <xsl:param name="i18n.codi_referencia" />
    <xsl:param name="i18n.codi_classificacio" />
    <xsl:param name="i18n.titol_serie_documental"/>
    <xsl:param name="i18n.numero_document"/>
    <xsl:param name="i18n.data_creacio"/>
    <xsl:param name="i18n.nivell_descripcio"/>
    <xsl:param name="i18n.suport"/>
    <xsl:param name="i18n.descripcio"/>
    <xsl:param name="i18n.descriptors"/>    
    <xsl:param name="i18n.nom_productor"/>
    <xsl:param name="i18n.unitat_productora"/>
    <xsl:param name="i18n.tipus_document"/>
    <xsl:param name="i18n.classificacio_seguretat_acces"/>
    <xsl:param name="i18n.nivell_classificacio_evidencial"/>
    <xsl:param name="i18n.document_essencial"/>
    <xsl:param name="i18n.sensibilitat_dades_LOPD"/>
    
    <xsl:template match="/">
        <div class="descriptiveMetadata">
            <xsl:apply-templates />
        </div>
    </xsl:template>
    <xsl:template match="/voc:document">
        <field name="level">
           UDS
        </field>
        <field name="level_txt">
           UDS
        </field>                
        <xsl:if test="voc:titol">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:titol">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.title"/>
                    </div>
                    <xsl:for-each select="voc:titol">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:codi_referencia">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:codi_referencia">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.codi_referencia"/>
                    </div>
                    <xsl:for-each select="voc:codi_referencia">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:numero_document">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:numero_document">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.numero_document"/>
                    </div>
                    <xsl:for-each select="voc:numero_document">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:codi_classificacio">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:codi_classificacio">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.codi_classificacio"/>
                    </div>
                    <xsl:for-each select="voc:codi_classificacio">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:titol_serie_documental">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:titol_serie_documental">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.titol_serie_documental"/>
                    </div>
                    <xsl:for-each select="voc:titol_serie_documental">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:nivell_descripcio">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:nivell_descripcio">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.nivell_descripcio"/>
                    </div>
                    <xsl:for-each select="voc:nivell_descripcio">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:suport">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:suport">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.suport"/>
                    </div>
                    <xsl:for-each select="voc:suport">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:data_creacio">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:data_creacio">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.data_creacio"/>
                    </div>
                    <xsl:for-each select="voc:data_creacio">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:nom_productor">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:nom_productor">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.nom_productor"/>
                    </div>
                    <xsl:for-each select="voc:nom_productor">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:unitat_productora">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:unitat_productora">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.unitat_productora"/>
                    </div>
                    <xsl:for-each select="voc:unitat_productora">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:descripcio">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:descripcio">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.descripcio"/>
                    </div>
                    <xsl:for-each select="voc:descripcio">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:descriptors">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:descriptors">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.descriptors"/>
                    </div>
                    <xsl:for-each select="voc:descriptors">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:classificacio_seguretat_acces">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:classificacio_seguretat_acces">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.classificacio_seguretat_acces"/>
                    </div>
                    <xsl:for-each select="voc:classificacio_seguretat_acces">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:sensibilitat_dades_LOPD">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:sensibilitat_dades_LOPD">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.sensibilitat_dades_LOPD"/>
                    </div>
                    <xsl:for-each select="voc:sensibilitat_dades_LOPD">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:nivell_classificacio_evidencial">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:nivell_classificacio_evidencial">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.nivell_classificacio_evidencial"/>
                    </div>
                    <xsl:for-each select="voc:nivell_classificacio_evidencial">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:document_essencial">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:document_essencial">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.document_essencial"/>
                    </div>
                    <xsl:for-each select="voc:document_essencial">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:tipus_document">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:tipus_document">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.tipus_document"/>
                    </div>
                    <xsl:for-each select="voc:tipus_document">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
    </xsl:template>
</xsl:stylesheet>


