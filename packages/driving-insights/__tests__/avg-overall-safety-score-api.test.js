import {
  ALL_OCCUPANT_ROLES,
  ALL_TRANSPORT_MODES,
  VALID_OCCUPANT_ROLES,
  VALID_PERIODS,
  VALID_TRANSPORT_MODES
} from "../lib/avg-overall-safety-score-api";

describe("Average overall safety score API tests", () => {
  let api;

  beforeEach(() => {
    jest.resetModules();
    api = require("../lib/avg-overall-safety-score-api").default;
  });

  describe("throws an error if params is not an object", () => {
    // eslint-disable-next-line @typescript-eslint/no-empty-function
    test.each([1, [], "'value'", null, undefined, NaN, function() {
    }])("%s", async (value) => {
      await expect(api).rejects.toThrowError("Expected safety score parameters to be an object.");
    });
  });

  test("throws an error if the provided period value is not valid", async () => {
    const nativeModule = {};
    const params = { period: 13 };
    expect(VALID_PERIODS).not.toContain(params.period);
    await expect(api(nativeModule, params)).rejects
      .toThrowError(`getAverageOverallSafetyScore was called with an invalid period value (${params.period}), but the supported values are ${VALID_PERIODS.join(", ")}`);
  });

  test("throws an error if the provided transport modes are not in an array", async () => {
    const nativeModule = {};
    const params = {
      period: VALID_PERIODS[0],
      transportModes: "CAR"
    };
    await expect(api(nativeModule, params)).rejects
      .toThrowError("Expected transport mode values should be provided in an array.");
  });

  test("throws an error if the provided transport modes contain invalid values", async () => {
    const nativeModule = {};
    const invalidTransportModes = ["TELEPORT", "HORSEBACK"];
    const params = {
      period: VALID_PERIODS[0],
      transportModes: VALID_TRANSPORT_MODES.concat(invalidTransportModes)
    };
    await expect(api(nativeModule, params)).rejects
      .toThrowError(`getAverageOverallSafetyScore was called with the following invalid transport modes:
        ${invalidTransportModes.join(", ")}, but the supported transport modes are ${VALID_TRANSPORT_MODES.join(", ")}`);
  });

  test("throws an error if the provided occupant roles are not in an array", async () => {
    const nativeModule = {};
    const params = {
      period: VALID_PERIODS[0],
      transportModes: VALID_TRANSPORT_MODES,
      occupantRoles: "DRIVER"
    };
    await expect(api(nativeModule, params)).rejects
      .toThrowError("Expected occupant role values should be provided in an array.");
  });

  test("throws an error if the provided occupant roles contain invalid values", async () => {
    const nativeModule = {};
    const invalidOccupantRoles = ["SPECTATOR"];
    const params = {
      period: VALID_PERIODS[0],
      transportModes: VALID_TRANSPORT_MODES,
      occupantRoles: VALID_OCCUPANT_ROLES.concat(invalidOccupantRoles)
    };
    await expect(api(nativeModule, params)).rejects
      .toThrowError(`getAverageOverallSafetyScore was called with the following invalid occupant roles:
        ${invalidOccupantRoles.join(", ")}, but the supported occupant roles are ${VALID_OCCUPANT_ROLES.join(", ")}`);
  });

  test("assumes all transport modes and all occupant roles were provided if invoked with no transport modes nor occupant roles",
    async () => {
      const expectedScore = 0.76;
      const mockGetAverageOverallSafetyScore = jest.fn();
      mockGetAverageOverallSafetyScore.mockResolvedValueOnce(expectedScore);

      const nativeModule = {
        getAverageOverallSafetyScore: mockGetAverageOverallSafetyScore
      };
      const params = {
        period: VALID_PERIODS[0]
      };

      await expect(api(nativeModule, params)).resolves.toEqual(expectedScore);
      expect(mockGetAverageOverallSafetyScore).toHaveBeenCalledWith({
        period: VALID_PERIODS[0],
        transportModes: VALID_TRANSPORT_MODES,
        occupantRoles: VALID_OCCUPANT_ROLES
      });
    });

  test("assumes all transport modes and all occupant roles were provided if invoked with all transport modes and all occupant roles explicitly",
    async () => {
      const expectedScore = 0.76;
      const mockGetAverageOverallSafetyScore = jest.fn();
      mockGetAverageOverallSafetyScore.mockResolvedValueOnce(expectedScore);

      const nativeModule = {
        getAverageOverallSafetyScore: mockGetAverageOverallSafetyScore
      };
      const params = {
        period: VALID_PERIODS[0],
        transportModes: ALL_TRANSPORT_MODES,
        occupantRoles: ALL_OCCUPANT_ROLES
      };

      await expect(api(nativeModule, params)).resolves.toEqual(expectedScore);
      expect(mockGetAverageOverallSafetyScore).toHaveBeenCalledWith({
        period: VALID_PERIODS[0],
        transportModes: VALID_TRANSPORT_MODES,
        occupantRoles: VALID_OCCUPANT_ROLES
      });
    });

  test("captures the few provided transport modes and occupant roles",
    async () => {
      const expectedScore = 0.76;
      const mockGetAverageOverallSafetyScore = jest.fn();
      mockGetAverageOverallSafetyScore.mockResolvedValueOnce(expectedScore);

      const nativeModule = {
        getAverageOverallSafetyScore: mockGetAverageOverallSafetyScore
      };
      const inputTransportModes = ["CAR", "TRAM"];
      const inputOccupantRoles = ["DRIVER", "PASSENGER"];
      const params = {
        period: VALID_PERIODS[0],
        transportModes: inputTransportModes,
        occupantRoles: inputOccupantRoles
      };

      await expect(api(nativeModule, params)).resolves.toEqual(expectedScore);
      expect(mockGetAverageOverallSafetyScore).toHaveBeenCalledWith({
        period: VALID_PERIODS[0],
        transportModes: inputTransportModes,
        occupantRoles: inputOccupantRoles
      });
    });
});


