/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import { html, PolymerElement } from '@polymer/polymer/polymer-element';
import '@vaadin/vaadin-text-field';

class TemplatePolymer extends PolymerElement {

    static get is() {
        return 'template-polymer';
    }

    static get properties() {
        return {

        };
    }

    static get styles() {
        return css`
            :host .container {
                padding: 2px;
            }

            :host {
                display: inline-block;
                width: 100%;
            }
        `;
    }

    static get template() {
         return html`
            <style>
                :host .container {
                    padding: 2px;
                }

                :host {
                    display: inline-block;
                    width: 100%;
                }
            </style>
            <div class="container">
               <vaadin-text-field id="textField"> </vaadin-text-field>
            </div>
         `;
    }
}

customElements.define(TemplatePolymer.is, TemplatePolymer);
