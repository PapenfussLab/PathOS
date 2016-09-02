/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

import com.aspose.words.Body
import com.aspose.words.ControlChar
import com.aspose.words.DocumentVisitor
import com.aspose.words.FieldEnd
import com.aspose.words.FieldSeparator
import com.aspose.words.FieldStart
import com.aspose.words.HeaderFooter
import com.aspose.words.Paragraph
import com.aspose.words.Run
import com.aspose.words.VisitorAction
import groovy.util.logging.Log4j

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Visitor Class for traversing a Word document and rendering it to text
 *
 *  01  Ken Doig    15-dec-15   Initial create
 */

/**
 * Simple implementation of saving a document in the plain text format. Implemented as a Visitor.
 */
@Log4j
public class DocToTxtWriter extends DocumentVisitor
{
    private final   StringBuilder   mBuilder
    private         boolean         mIsSkipText
    private         Map             reportMap

    public DocToTxtWriter() throws Exception
    {
        mIsSkipText = false
        mBuilder    = new StringBuilder()
        reportMap   = [:]
    }

    /**
     * Gets the plain text of the document that was accumulated by the visitor.
     */
    public String getText() throws Exception
    {
        return mBuilder.toString()
    }

    /**
     * Gets the Map of the document that was accumulated by the visitor.
     */
    public Map getMap() throws Exception
    {
        return reportMap
    }

    /**
     * Called when a Run node is encountered in the document.
     */
    public int visitRun(Run run) throws Exception
    {
        appendText(run.getText());

        // Let the visitor continue visiting other nodes.
        //
        return VisitorAction.CONTINUE;
    }

    /**
     * Called when a FieldStart node is encountered in the document.
     */
    public int visitFieldStart(FieldStart fieldStart) throws Exception
    {
        // In Microsoft Word, a field code (such as "MERGEFIELD FieldName") follows
        // after a field start character. We want to skip field codes and output field
        // result only, therefore we use a flag to suspend the output while inside a field code.
        //
        // Note this is a very simplistic implementation and will not work very well
        // if you have nested fields in a document.
        mIsSkipText = true;

        return VisitorAction.CONTINUE;
    }

    /**
     * Called when a FieldSeparator node is encountered in the document.
     */
    public int visitFieldSeparator(FieldSeparator fieldSeparator) throws Exception
    {
        // Once reached a field separator node, we enable the output because we are
        // now entering the field result nodes.
        mIsSkipText = false;

        return VisitorAction.CONTINUE;
    }

    /**
     * Called when a FieldEnd node is encountered in the document.
     */
    public int visitFieldEnd(FieldEnd fieldEnd) throws Exception
    {
        // Make sure we enable the output when reached a field end because some fields
        // do not have field separator and do not have field result.
        mIsSkipText = false;

        return VisitorAction.CONTINUE;
    }

    /**
     * Called when visiting of a Paragraph node is ended in the document.
     */
    public int visitParagraphEnd(Paragraph paragraph) throws Exception
    {
        // When outputting to plain text we output a linefeed
        //
        appendText(ControlChar.LF);

        return VisitorAction.CONTINUE;
    }

    public int visitBodyStart(Body body) throws Exception
    {
        return VisitorAction.CONTINUE;
    }

    public int visitBodyEnd(Body body) throws Exception
    {
        return VisitorAction.CONTINUE
    }

    /**
     * Called when a HeaderFooter node is encountered in the document.
     */
    public int visitHeaderFooterStart(HeaderFooter headerFooter) throws Exception
    {
        // Returning this value from a visitor method causes visiting of this
        // node to stop and move on to visiting the next sibling node.
        // The net effect in this example is that the text of headers and footers
        // is not included in the resulting output.
        return VisitorAction.SKIP_THIS_NODE
    }

    /**
     * Adds text to the current output. Honours the enabled/disabled output flag.
     */
    private void appendText(String text) throws Exception
    {
        if ( ! mIsSkipText )
        {
            mBuilder.append(text)
        }
    }
}