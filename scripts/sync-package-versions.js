function updateVersionNumberForTopLevelModule() {
  const saveFile = require('fs').writeFileSync;
  const pkgJsonPath = `${require.main.paths[0].split('node_modules')[0]}../package.json`;
  console.log('Found top level package.json file: ' + pkgJsonPath);
  const json = updatePackageJsonVersionNumber(pkgJsonPath);
  saveFile(pkgJsonPath, JSON.stringify(json, null, 2));
}

function updateVersionNumbersForJsModule(moduleName) {
  const saveFile = require('fs').writeFileSync;
  const pkgJsonPath = `${require.main.paths[0].split('node_modules')[0]}../packages/${moduleName}/package.json`;
  console.log('Found package.json file: ' + pkgJsonPath);
  let json = updatePackageJsonVersionNumber(pkgJsonPath);
  json = updatePackageJsonPeerDependenciesVersionNumbers(json);
  saveFile(pkgJsonPath, JSON.stringify(json, null, 2));
}

function updateVersionNumberForIosModule(module) {
  const jsModuleName = module.name;
  const iosModuleName = module.iosModuleName;
  const readFile = require('fs').readFile;
  const saveFile = require('fs').writeFileSync;
  const modulePodspecPath = `${require.main.paths[0].split('node_modules')[0]}../packages/${jsModuleName}/${iosModuleName}.podspec`;
  console.log('Found podspec file: ' + modulePodspecPath);
  readFile(modulePodspecPath, 'utf8', function (error, text) {
    const match = text.match('(?<prefix>s.version\\s+=\\s+)"(?<versionCode>.+)"');
    const prefix = match.groups.prefix;
    const versionCode = match.groups.versionCode;
    const newText = text.replace(`${prefix}"${versionCode}"`, `${prefix}"${newVersion}"`);
    saveFile(modulePodspecPath, newText, 'utf8');
  });
}

function updatePackageJsonVersionNumber(pkgJsonPath) {
  const json = require(pkgJsonPath);
  if (json.hasOwnProperty('version')) {
    json['version'] = newVersion;
  }
  return json;
}

function updatePackageJsonPeerDependenciesVersionNumbers(json) {
  if (json.hasOwnProperty('peerDependencies')) {
    const peerDeps = Object.entries(json.peerDependencies);
    if (peerDeps.length) {
      const sentianceDeps = peerDeps.filter(peerDep => {
        const [peerDepName, _] = peerDep;
        return peerDepName.startsWith(SENTIANCE_ORG_NAME);
      }).map(dep => {
        const [peerDepName, _] = dep;
        return peerDepName;
      });

      sentianceDeps.forEach(sentianceDep => {
        json.peerDependencies[sentianceDep] = newVersion;
      });
    }
  }

  return json;
}

function updateVersionNumbersForModule(module) {
  updateVersionNumbersForJsModule(module.name);
  if (module.iosModuleName !== undefined) {
    updateVersionNumberForIosModule(module);
  }
}

const SENTIANCE_ORG_NAME = "@sentiance-react-native";
let newVersion = process.env.npm_config_newversion;
let isNewVersionSpecified = true;

if (!newVersion) {
  console.info('A new version number has not been specified. Falling back to the number indicated on the top level package.json');
  newVersion = process.env.npm_package_version;
  console.info('The new version will be: ' + newVersion);
  isNewVersionSpecified = false;
}

const TARGET_MODULES = [
  {
    name: 'core',
    iosModuleName: 'RNSentianceCore'
  },
  {
    name: 'crash-detection'
  },
  {
    name: 'user-context'
  },
  {
    name: 'legacy'
  },
  {
    name: 'driving-insights'
  },
];

if (isNewVersionSpecified) {
  updateVersionNumberForTopLevelModule();
}

console.log(`Updating version numbers for ${TARGET_MODULES.length} JS module(s): ${TARGET_MODULES.map(module => module.name).join(', ')}`);
TARGET_MODULES.forEach(module => updateVersionNumbersForModule(module));
