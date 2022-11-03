/**
 * This script attempts to figure out what the tag for the version could be.
 * This tag would ideally be used in the "npm publish" of the package.
 * 
 * 1.1.1 tag = stable
 * 1.1.1-beta.0 = beta
 * etc
 */

const json = require('../package.json')

const match = json.version.match(/^(\d*\.\d*\.\d*)\-([a-z]*)\.(\d*)$/)
const tag = (match && match[2]) ?? 'latest'

/** Logs the tag for it to be consumed by a different script as an input */
console.log(tag)