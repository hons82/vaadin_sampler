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
import { repeat } from "lit-html/directives/repeat";
import '@vaadin/vaadin-text-field';
import '@vaadin/vaadin-list-box';
import '@vaadin/vaadin-item';
import '@vaadin/vaadin-overlay';
import '@vaadin/vaadin-button';
import '@vaadin/vaadin-lumo-styles/icons';
import '@polymer/iron-icon';
import './vcf-autosuggest-overlay';

/**
 * `<vcf-autosuggest>` Web Component with a text input that provides a panel of suggested options.
 * Provides features such as advanced customization, lazy loading and label generator.
 *
 * ```html
 * <vcf-autosuggest></vcf-autosuggest>
 * ```
 *
 * @memberof Vaadin
 * @mixes ElementMixin
 * @mixes ThemableMixin
 * @demo demo/index.html
 */
export class VcfAutosuggest extends LitElement {

	static get styles() {
		return css`
			:host .container {
				padding: 2px;
			}

			:host {
				display: inline-block;
			}
		`;
	}

	render() {
     	return html`
			<div class="container">
				<slot name="textField"></slot>
				<vcf-autosuggest-overlay id="autosuggestOverlay">
					<vaadin-list-box id="optionsContainer" part="options-container" style="margin: 0;">
						${this.loading ? this.renderLoading() : this.renderOptions()}
						${this._showNoResultsItem ? this.renderNoResults() : null}
						${this._showInputLengthBelowMinimumItem ? this.renderInputLengthBelowMinimum() : null}
					</vaadin-list-box>
					<div id="dropdownEndSlot" part="dropdown-end-slot" style="padding-left: 0.5em; padding-right: 0.5em;">
						<slot name="dropdownEndSlot"></slot>
					</div>
				</vcf-autosuggest-overlay>
			</div>
     	`;
	}

	renderLoading() {
		return html`
			<vaadin-item disabled part="option" class="loading">
				<div part="loading-indicator"></div>
			</vaadin-item>
		`;
	}

	renderNoResults() {
		return html`
			<vaadin-item disabled part="option" class="no-results">
				<div part="no-results"></div>
			</vaadin-item>
		`;
	}

	renderInputLengthBelowMinimum() {
		return html`
			<vaadin-item disabled part="option" class="input-length-below-minimum">
				<div part="input-length-below-minimum"></div>
			</vaadin-item>
		`;
	}

	renderOptions() {
		return html`
			${repeat(this._optionsToDisplay, (option) => option.optId, (option, index) => html`
				<vaadin-item part="option" data-oid="${option.optId}" data-key="${option.key}">
					${index}:
					${this._getSuggestedStart(this.inputValue, option)}<span part="bold">${this._getInputtedPart(this.inputValue, option)}</span>${this._getSuggestedEnd(this.inputValue, option)}
				</vaadin-item>
		  		`
			)}
		`;
	}

	static get is() {
        return 'vcf-autosuggest';
    }

 	static get properties() {
    	return {
			lazy: { type: Boolean },
			limit: { type: Number },
			opened: { type: Boolean },
			loading: { type: Boolean },
			openDropdownOnClick: { type: Boolean },
			disableSearchHighlighting: { type: Boolean},
			minimumInputLengthToPerformLazyQuery: { type: Boolean },
			customizeOptionsForWhenValueIsNull: { type: Boolean },
			searchMatchingMode: { type: String },
			inputValue: { type: String },
			options: { type: Array },
			defaultOption: { type: Object },

			_showNoResultsItem: { Boolean },
			_showInputLengthBelowMinimumItem: { Boolean },
			_noResultsMsg: { String },
			_inputLengthBelowMinimumMsg: { String },
			_optionsToDisplay: { Array },
			_boundOutsideClickHandler: { Object },
			_boundSetOverlayPosition: { Object }
		};
	}

	constructor() {
        super();
		this.inputValue = '';
		this.options = [];
		this._optionsToDisplay = [];
		this.searchMatchingMode = 'STARTS_WITH';
        this._boundSetOverlayPosition = this._setOverlayPosition.bind(this);
        this._boundOutsideClickHandler = this._outsideClickHandler.bind(this);
    }

	connectedCallback() {
        super.connectedCallback();
        document.addEventListener('click', this._boundOutsideClickHandler);
    }

	firstUpdated() {
        // this.$.textField.addEventListener('input', this._onInput.bind(this));
        this.addEventListener('iron-resize', this._boundSetOverlayPosition);
        this.addEventListener('click', this._elementClickListener);
        this.addEventListener('blur', this._elementBlurListener);
        // this.addEventListener('keydown', this._onKeyDown.bind(this));
        this._overlayElement = this.shadowRoot.getElementById('autosuggestOverlay'); // not null
		this.$server.somethingHappened();
        // this._optionsContainer = this.$.optionsContainer;
        this._overlayElement.addEventListener('vaadin-overlay-outside-click', ev => ev.preventDefault());
        // this._dropdownEndSlot = this.$.dropdownEndSlot;
        // this._dropdownEndSlot.addEventListener('click', ev => { ev.preventDefault(); ev.stopPropagation(); });
		if (this._noResultsMsg) this.setNoResultsMessage(this._noResultsMsg);
		if (this._inputLengthBelowMinimumMsg) this.setInputLengthBelowMinimumMessage(this._inputLengthBelowMinimumMsg);
    }

	disconnectedCallback() {
        super.disconnectedCallback();
        document.removeEventListener('click', this._boundOutsideClickHandler);
    }

	updated(changedProperties) {
		changedProperties.forEach((oldValue, propName) => {
			const newValue = JSON.stringify(this[propName]);
			console.log(`${propName} changed. oldValue: ${oldValue} newValue: ${newValue}`);
			if (propName == "opened") {
				this._openedChange(newValue === 'true');
			} else if (propName == 'options') {
				this._refreshOptionsToDisplay(this.options, this.input);
			}
		});
		return true;
	}

	_elementClickListener(event) {
        if(this.openDropdownOnClick) this.opened = true;
        event.stopPropagation();
    }

	_outsideClickHandler() {
        if(!this.opened) return;
        // this._applyValue(this.selectedValue == null ? (this._hasDefaultOption() ? this._defaultOption.key : '') : this.selectedValue);
        this.opened = false;
    }

	_openedChange(opened) {
		this._overlayElement.opened = opened
        if (opened) {
			this._setOverlayPosition();
			this._refreshOptionsToDisplay(this.options, this.inputValue);
		}
	}

	_setOverlayPosition() {
		// TODO: Find a better solution to find the textfield
        const inputRect = this.shadowRoot.querySelector('slot[name=textField]').assignedNodes({flatten: true})[0].getBoundingClientRect();
        if (this._overlayElement != null) {
            this._overlayElement.style.left = inputRect.left + 'px';
            this._overlayElement.style.top = inputRect.bottom + window.pageYOffset + 'px';
            this._overlayElement.updateStyles({ '--vcf-autosuggest-options-width': inputRect.width + 'px' });
        }
    }

	setNoResultsMessage(msg) {
		if (this._overlayElement != null) {
			this._overlayElement.updateStyles({ '--x-no-results-msg': '\'' + msg + '\'' });
			this._noResultsMsg = null;
		} else {
			this._noResultsMsg = msg;
		}
	}

	setInputLengthBelowMinimumMessage(msg) {
		if (this._overlayElement != null) {
			this._overlayElement.updateStyles({ '--x-input-length-below-minimum-msg': '\'' + msg + '\'' });
			this._inputLengthBelowMinimumMsg = null;
		} else {
			this._inputLengthBelowMinimumMsg = msg;
		}
	}

	_loadingChanged(v) {
        this.loading = !v
        this.loading = v //FORCE RE-RENDER
        this._refreshMessageItemsState();
    }

	_refreshOptionsToDisplay(options, value) {
        if(typeof value === 'undefined') value = null;
        let _res = [];
        if(this.customizeOptionsForWhenValueIsNull && (value == null || value.length == 0 || value.trim() == (this._hasDefaultOption() ? this.defaultOption.label : '').trim()))
            _res = _res.concat(this._limitOptions(this.optionsForWhenValueIsNull));
        else _res = _res.concat(this._limitOptions(this._filterOptions(options, value)));
        if(!_res || _res==null) _res = [];

        // // Criteria for showing the default option:
        // // 1. The input value is "" and the default value's key is not present in the optionsForWhenValueIsNull list
        // // 2. It matches with the default option and the key is not in the results already
        // if(this._hasDefaultOption()) {
        //     if(value.length == 0) {
        //         if(!this.customizeOptionsForWhenValueIsNull) _res.unshift({label: this._defaultOption.label, searchStr: this._defaultOption.searchStr, key: this._defaultOption.key});
        //         else if(_res.filter(opt => opt.key == this._defaultOption.key).length == 0) _res.unshift({label: this._defaultOption.label, searchStr: this._defaultOption.searchStr, key: this._defaultOption.key});
        //     }
        //     if(value.length > 0 && this._filterOptions([this._defaultOption], value).length > 0 && _res.filter(opt => opt.key == this._defaultOption.key).length == 0)
        //         _res.unshift({label: this._defaultOption.label, searchStr: this._defaultOption.searchStr, key: this._defaultOption.key});
        // }

        for(let i=0; i<_res.length; i++) { _res[i].optId = i; }
        this._optionsToDisplay = _res;
        this._loadingChanged(false);
    }

	_hasDefaultOption() {
        return (this.defaultOption != null && this.defaultOption.key != null);
    }

	_limitOptions(options) {
        if(!options) return [];
        if(this.limit != null) return options.slice(0, this.limit);
        else return options;
    }

	_filterOptions(opts, v) {
        if(v == null || v.trim().length == 0 || v.trim() == (this._hasDefaultOption() ? this.defaultOption.label : '').trim()) return opts;
        let res = opts.filter(opt => {
            switch(this.searchMatchingMode) {
                case "CONTAINS":
                    return this.caseSensitive ? opt.searchStr.trim().includes(v.trim()) : opt.searchStr.trim().toLowerCase().includes(v.toLowerCase());
                case "STARTS_WITH":
                    return this.caseSensitive ? opt.searchStr.trim().startsWith(v.trim()) : opt.searchStr.trim().toLowerCase().startsWith(v.trim().toLowerCase());
                default:
                    return false;
            }
        });
        return res;
    }

	_refreshMessageItemsState() {
        if (!(this.lazy && this.minimumInputLengthToPerformLazyQuery > 0)) {
            this._showNoResultsItem = this._optionsToDisplay.length == 0 && !this.loading;
            this._showInputLengthBelowMinimumItem = false;
        } else {
            this._showNoResultsItem = this._optionsToDisplay.length == 0 && !this.loading && this.inputValue.length >= this.minimumInputLengthToPerformLazyQuery;
            if(!this._showNoResultsItem && !this.loading && this._optionsToDisplay.length == 0){
                this._showInputLengthBelowMinimumItem = true;
            } else {
                this._showInputLengthBelowMinimumItem = false;
            }
        }
    }

	_getSuggestedStart(value, option) {
        if (this.disableSearchHighlighting || !option.label || !value) return;
        if (option.label.trim().length == 0) return;
        return option.label.substr(0, this._getValueIndex(value, option));
    }

    _getInputtedPart(value, option) {
        if (this.disableSearchHighlighting || !option || !option.label || !value) return;
        if (option.label.trim().length == 0) return;
        if (!value) return option.label;
        return option.label.substr(this._getValueIndex(value, option), value.length);
    }

    _getSuggestedEnd(value, option) {
        if (this.disableSearchHighlighting) return option.label;
        if (!option.label) return;
        if (option.label && option.label.trim().length == 0) return;
        return option.label.substr(this._getValueIndex(value, option) + value.length, option.searchStr.length);
    }

	_getValueIndex(value, option) {
        return option.label.toLowerCase().indexOf(value.toLowerCase()) >= 0 ? option.label.toLowerCase().indexOf(value.toLowerCase()) : 0;
    }
}

customElements.define(VcfAutosuggest.is, VcfAutosuggest);
