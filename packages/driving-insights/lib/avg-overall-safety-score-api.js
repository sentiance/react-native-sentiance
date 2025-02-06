export const VALID_TRANSPORT_MODES = [
  "UNKNOWN", "BICYCLE", "WALKING", "RUNNING", "TRAM", "TRAIN", "CAR", "BUS", "MOTORCYCLE"];
export const VALID_OCCUPANT_ROLES = ["DRIVER", "PASSENGER", "UNAVAILABLE"];
export const VALID_PERIODS = [7, 14, 30];

export const ALL_TRANSPORT_MODES = "ALL_MODES";
export const ALL_OCCUPANT_ROLES = "ALL_ROLES";

export default function getAverageOverallSafetyScore(nativeModule, safetyScoreParams) {
  // This is the validated input that will be provided to our native modules.
  const validatedParams = {};

  if (!isObject(safetyScoreParams)) {
    return Promise.reject(new Error(`Expected safety score parameters to be an object.`));
  }

  const period = safetyScoreParams.period;
  if (!isValidPeriod(period)) {
    return Promise.reject(new Error(
      `getAverageOverallSafetyScore was called with an invalid period value (${period}), but the supported values are ${VALID_PERIODS.join(', ')}`
    ));
  }
  validatedParams.period = period;

  let transportModes = safetyScoreParams.transportModes;
  if (transportModes && transportModes !== ALL_TRANSPORT_MODES) {
    // If the user did provide any transport mode values, we validate them
    if (!Array.isArray(transportModes)) {
      return Promise.reject(new Error("Expected transport mode values should be provided in an array."));
    }

    const invalidTransportModes = extractInvalidTransportModes(transportModes);
    if (invalidTransportModes.length > 0) {
      return Promise.reject(new Error(
        `getAverageOverallSafetyScore was called with the following invalid transport modes:
        ${invalidTransportModes.join(', ')}, but the supported transport modes are ${VALID_TRANSPORT_MODES.join(', ')}`
      ));
    }
    validatedParams.transportModes = transportModes;
  } else {
    // The user did not provide transport modes or specified a value of 'ALL_MODES'
    validatedParams.transportModes = VALID_TRANSPORT_MODES;
  }

  const occupantRoles = safetyScoreParams.occupantRoles;
  if (occupantRoles && occupantRoles !== ALL_OCCUPANT_ROLES) {
    // If the user did provide any occupant role values, we validate them
    if (!Array.isArray(occupantRoles)) {
      return Promise.reject(new Error("Expected occupant role values should be provided in an array."));
    }
    const invalidOccupantRoles = extractInvalidOccupantRoles(occupantRoles);
    if (invalidOccupantRoles.length > 0) {
      return Promise.reject(new Error(
        `getAverageOverallSafetyScore was called with the following invalid occupant roles:
        ${invalidOccupantRoles.join(', ')}, but the supported occupant roles are ${VALID_OCCUPANT_ROLES.join(', ')}`
      ));
    }
    validatedParams.occupantRoles = occupantRoles;
  } else {
    // The user did not provide occupant roles or specified a value of 'ALL_ROLES'
    validatedParams.occupantRoles = VALID_OCCUPANT_ROLES;
  }

  return nativeModule.getAverageOverallSafetyScore(validatedParams);
}

const isObject = (obj) => obj != null && obj.constructor.name === "Object";

const isValidPeriod = (period) => period && VALID_PERIODS.includes(period);

const extractInvalidTransportModes = (transportModes) => {
  return transportModes.filter(transportMode => !VALID_TRANSPORT_MODES.includes(transportMode));
};

const extractInvalidOccupantRoles = (occupantRoles) => {
  return occupantRoles.filter(occupantRole => !VALID_OCCUPANT_ROLES.includes(occupantRole));
};
