/*
 Copyright 2013-2015 Jason Leyba

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

/**
 * @constructor
 * @final
 */
var Descriptor = function() {};

/** @type {string} */
Descriptor.prototype.name = '';

/** @type {string} */
Descriptor.prototype.qualifiedName = '';

/** @type {boolean} */
Descriptor.prototype.namespace = false;

/** @type {boolean} */
Descriptor.prototype.interface = false;

/** @type {string} */
Descriptor.prototype.href = '';

/** @type {Array<!Descriptor>} */
Descriptor.prototype.statics = null;

/** @type {Array<!Descriptor>} */
Descriptor.prototype.members = null;

/** @type {Array<!Descriptor>} */
Descriptor.prototype.types = null;


/**
 * @constructor
 * @final
 */
var TypeRegistry = function() {};

/** @type {Array<!Descriptor>} */
TypeRegistry.prototype.types = null;

/** @type {Array<!Descriptor>} */
TypeRegistry.prototype.modules = null;
