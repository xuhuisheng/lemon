/**
 █▒▓▒░ The FlexPaper Project

 This file is part of FlexPaper.

 FlexPaper is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, version 3 of the License.

 FlexPaper is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with FlexPaper.  If not, see <http://www.gnu.org/licenses/>.

 For more information on FlexPaper please see the FlexPaper project
 home page: http://flexpaper.devaldi.com
 */

$(function() {
    /**
     * Handles the event of external links getting clicked in the document.
     *
     * @example onExternalLinkClicked("http://www.google.com")
     *
     * @param String link
     */
    jQuery('#documentViewer').bind('onExternalLinkClicked',function(e,link){
        jQuery("#txt_eventlog").val('onExternalLinkClicked:' + link + '\n' + jQuery("#txt_eventlog").val());
        window.open(link,'_flexpaper_exturl');
    });

    /**
     * Recieves progress information about the document being loaded
     *
     * @example onProgress( 100,10000 );
     *
     * @param int loaded
     * @param int total
     */
    jQuery('#documentViewer').bind('onProgress',function(e,loadedBytes,totalBytes){
        jQuery("#txt_progress").val('onProgress:' + loadedBytes + '/' + totalBytes + '\n');
    });

    /**
     * Handles the event of a document is in progress of loading
     *
     */
    jQuery('#documentViewer').bind('onDocumentLoading',function(e){
        jQuery("#txt_eventlog").val('onDocumentLoading' + '\n' + jQuery("#txt_eventlog").val());
    });

    /**
     * Handles the event of a document is in progress of loading
     *
     */
    jQuery('#documentViewer').bind('onPageLoading',function(e,pageNumber){
        jQuery("#txt_eventlog").val('onPageLoading:' + pageNumber + '\n' + jQuery("#txt_eventlog").val());
    });

    /**
     * Receives messages about the current page being changed
     *
     * @example onCurrentPageChanged( 10 );
     *
     * @param int pagenum
     */
    jQuery('#documentViewer').bind('onCurrentPageChanged',function(e,pagenum){
        jQuery("#txt_eventlog").val('onCurrentPageChanged:' + pagenum + '\n' + jQuery("#txt_eventlog").val());
    });

    /**
     * Receives messages about the document being loaded
     *
     * @example onDocumentLoaded( 20 );
     *
     * @param int totalPages
     */
    jQuery('#documentViewer').bind('onDocumentLoaded',function(e,totalPages){
        jQuery('#documentViewer').show();

        jQuery("#txt_eventlog").val('onDocumentLoaded:' + totalPages + '\n' + jQuery("#txt_eventlog").val());
    });

    /**
     * Receives messages about the page loaded
     *
     * @example onPageLoaded( 1 );
     *
     * @param int pageNumber
     */
    jQuery('#documentViewer').bind('onPageLoaded',function(e,pageNumber){
        jQuery("#txt_eventlog").val('onPageLoaded:' + pageNumber + '\n' + jQuery("#txt_eventlog").val());
    });

    /**
     * Receives messages about the page loaded
     *
     * @example onErrorLoadingPage( 1 );
     *
     * @param int pageNumber
     */
    jQuery('#documentViewer').bind('onErrorLoadingPage',function(e,pageNumber){
        jQuery("#txt_eventlog").val('onErrorLoadingPage:' + pageNumber + '\n' + jQuery("#txt_eventlog").val());
    });

    /**
     * Receives error messages when a document is not loading properly
     *
     * @example onDocumentLoadedError( "Network error" );
     *
     * @param String errorMessage
     */
    jQuery('#documentViewer').bind('onDocumentLoadedError',function(e,errMessage){
        jQuery("#txt_eventlog").val('onDocumentLoadedError:' + errMessage + '\n' + jQuery("#txt_eventlog").val());
    });

    /**
     * Receives error messages when a document has finished printed
     *
     * @example onDocumentPrinted();
     *
     */
    jQuery('#documentViewer').bind('onDocumentPrinted',function(e){
        jQuery("#txt_eventlog").val('onDocumentPrinted\n' + jQuery("#txt_eventlog").val());
    });
});