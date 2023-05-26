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
package org.girod.javafx.svgimage.xml.specs;

import org.girod.javafx.svgimage.xml.parsers.XMLNode;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.text.Text;

/**
 * Represents spans elements under a text element.
 *
 * @version 1.0
 */
public class SpanGroup {
   private Group textGroup;
   private List<TSpan> tspans = new ArrayList<>();

   public SpanGroup(Group textGroup) {
      this.textGroup = textGroup;
   }

   public Group getTextGroup() {
      return textGroup;
   }

   public List<TSpan> getSpans() {
      return tspans;
   }

   public void addTSpan(XMLNode node, Text text) {
      TSpan tspan = new TSpan(node, text);
      tspans.add(tspan);
   }

   public class TSpan {
      public final XMLNode node;
      public final Text text;

      private TSpan(XMLNode node, Text text) {
         this.node = node;
         this.text = text;
      }
   }
}
