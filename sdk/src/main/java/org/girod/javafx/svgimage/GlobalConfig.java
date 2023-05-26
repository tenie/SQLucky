/*
Copyright (c) 2021, 2022 Hervé Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://github.com/hervegirod/fxsvgimage
 */
package org.girod.javafx.svgimage;

import org.girod.javafx.svgimage.xml.parsers.SVGLibraryException;
import org.girod.javafx.svgimage.xml.parsers.SVGParsingException;

/**
 * The global configuration.
 *
 * @version 1.0
 */
public class GlobalConfig implements ExceptionsHandling {
   private static GlobalConfig config = null;
   private Boolean swingAvailable = null;
   private short exceptionsHandling = ExceptionsHandling.PRINT_EXCEPTION_MESSAGE;

   private GlobalConfig() {
   }

   /**
    * Return the unique instance.
    *
    * @return the unique instance
    */
   public static GlobalConfig getInstance() {
      if (config == null) {
         config = new GlobalConfig();
      }
      return config;
   }

   /**
    * Set the exceptions handling type.
    *
    * @param exceptionsHandling the exceptions handling type
    */
   public void setExceptionsHandling(short exceptionsHandling) {
      this.exceptionsHandling = exceptionsHandling;
   }

   /**
    * Return the exceptions handling type.
    *
    * @return the exceptions handling type
    */
   public short getExceptionsHandling() {
      return exceptionsHandling;
   }

   /**
    * Return true if swing is available.
    *
    * @return true if swing is available
    */
   public boolean isSwingAvailable() {
      if (swingAvailable == null) {
         try {
            Class.forName("org.girod.javafx.svgimage.AwtImageConverter", true, getClass().getClassLoader());
            swingAvailable = Boolean.TRUE;
         } catch (ClassNotFoundException ex) {
            swingAvailable = Boolean.FALSE;
         }
      }
      return swingAvailable;
   }

   /**
    * Handle an error message, depending on the value of the {@link #getExceptionsHandling()}. An exception will
    * be throw only if the value for the exceptions handling is {@link ExceptionsHandling#RETROW_EXCEPTION}.
    *
    * @param message the error message
    * @throws SVGLibraryException the rethrown exception
    */
   public void handleLibraryError(String message) throws SVGLibraryException {
      switch (exceptionsHandling) {
         case SKIP_EXCEPTION:
            return;
         case PRINT_EXCEPTION_MESSAGE:
            System.err.println(message);
            break;
         case RETROW_EXCEPTION:
            System.err.println(message);
            break;
         case PRINT_EXCEPTION_STACKTRACE:
            System.err.println(message);
            break;
         case RETROW_ALL:
            throw new SVGLibraryException(message);
         default:
            break;
      }
   }

   /**
    * Handle an error message, depending on the value of the {@link #getExceptionsHandling()}. An exception will
    * be throw only if the value for the exceptions handling is {@link ExceptionsHandling#RETROW_EXCEPTION}.
    *
    * @param message the error message
    * @throws SVGParsingException the rethrown exception
    */
   public void handleParsingError(String message) throws SVGParsingException {
      switch (exceptionsHandling) {
         case SKIP_EXCEPTION:
            return;
         case PRINT_EXCEPTION_MESSAGE:
            System.err.println(message);
            break;
         case RETROW_EXCEPTION:
            System.err.println(message);
            break;
         case PRINT_EXCEPTION_STACKTRACE:
            System.err.println(message);
            break;
         case RETROW_ALL:
            throw new SVGParsingException(message);
         default:
            break;
      }
   }

   /**
    * Handle a library exception, depending on the value of the {@link #getExceptionsHandling()}. An exception will
    * be throw only if the value for the exceptions handling is {@link ExceptionsHandling#RETROW_EXCEPTION}.
    *
    * @param th the Throwable
    * @throws SVGLibraryException the rethrown exception
    */
   public void handleLibraryException(Throwable th) throws SVGLibraryException {
      switch (exceptionsHandling) {
         case SKIP_EXCEPTION:
            return;
         case PRINT_EXCEPTION_MESSAGE:
            System.err.println(th.getMessage());
            break;
         case PRINT_EXCEPTION_STACKTRACE:
            th.printStackTrace();
            break;
         case RETROW_EXCEPTION:
            if (th instanceof SVGLibraryException) {
               throw (SVGLibraryException) th;
            } else {
               throw new SVGLibraryException(th);
            }
         default:
            break;
      }
   }

   /**
    * Handle a parsing exception, depending on the value of the {@link #getExceptionsHandling()}. An exception will
    * be throw only if the value for the exceptions handling is {@link ExceptionsHandling#RETROW_EXCEPTION}.
    *
    * @param th the Throwable
    * @throws SVGParsingException the rethrown exception
    */
   public void handleParsingException(Throwable th) throws SVGParsingException {
      switch (exceptionsHandling) {
         case SKIP_EXCEPTION:
            return;
         case PRINT_EXCEPTION_MESSAGE:
            System.err.println(th.getMessage());
            break;
         case PRINT_EXCEPTION_STACKTRACE:
            th.printStackTrace();
            break;
         case RETROW_EXCEPTION:
            if (th instanceof SVGLibraryException) {
               throw (SVGParsingException) th;
            } else {
               throw new SVGParsingException(th);
            }
         default:
            break;
      }
   }
}
