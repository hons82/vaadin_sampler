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

import { LitElement, html, css } from 'lit-element';
import '@vaadin/vaadin-text-field';

class TemplateComponent extends LitElement {

    static get is() {
        return 'template-component';
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

    render() {
         return html`
            <div class="container">
                <slot name="textField"></slot>
            </div>
         `;
    }

    firstUpdated() {
        this._textField = this.shadowRoot.querySelector('slot[name=textField]').assignedNodes({flatten: true})[0];
        this._textField.addEventListener('focus', this._textFieldFocused());
    }

    _textFieldFocus(focus=true) {
        if (focus)
            this._textField.focus();
        else
            this._textField.blur();
    }
}

customElements.define(TemplateComponent.is, TemplateComponent);
