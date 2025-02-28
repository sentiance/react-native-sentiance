module.exports = {
  clearMocks: true,
  moduleDirectories: [
    "./node_modules", // Prioritize top level node_modules over the package's. Makes sure the tests pick up the same top level defined RN.
    "<rootDir>/node_modules"
  ],
  preset: "react-native",
  transform: {
    "^.+\\.(js|jsx)$": 'babel-jest'
  },
};
