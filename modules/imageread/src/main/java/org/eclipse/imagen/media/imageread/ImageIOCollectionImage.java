/*
 * $RCSfile: ImageIOCollectionImage.java,v $
 *
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All  Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this  list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for
 * use in the design, construction, operation or maintenance of any
 * nuclear facility.
 *
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:01:54 $
 * $State: Exp $
 */
package org.eclipse.imagen.media.imageread;

import java.util.ArrayList;
import org.eclipse.imagen.CollectionImage;

/**
 * A <code>CollectionImage</code> to be used as the return value from <code>ImageReadCIF.create()</code> and <code>
 * ImageWriteCIF.create()</code>. The <code>imageCollection</code> instance variable is a <code>List</code>.
 */
class ImageIOCollectionImage extends CollectionImage {
    /**
     * Creates an <code>ImageIOCollectionImage</code> with the specified capacity.
     *
     * @exception IllegalArgumentException if <code>capacity</code> is not positive.
     */
    ImageIOCollectionImage(int capacity) {
        super();

        if (capacity <= 0) {
            // No message as this is not at the API level and it is
            // the unique exception.
            throw new IllegalArgumentException();
        }

        imageCollection = new ArrayList(capacity);
    }
}
