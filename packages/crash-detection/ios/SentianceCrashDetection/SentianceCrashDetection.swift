//
//  SentianceCrashDetection.swift
//  SentianceCrashDetection
//
//  Created by Mohammed Aouf Zouag on 18/03/2025.
//

import Foundation
import SENTSDK

@objc(SentianceCrashDetection)
class SentianceCrashDetection: NSObject {

    private let sdkNotInitializedError = (code: ESDKNotInitialized, message: "SDK is not initialized")
    private let sentiance = Sentiance.shared

    @objc
    func isVehicleCrashDetectionSupported(_ resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) {
        guard ensureSDKInitialized(reject) else { return }
        resolve(sentiance.isVehicleCrashDetectionSupported)
    }

    @objc
    func invokeDummyVehicleCrash(_ resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) {
        guard ensureSDKInitialized(reject) else { return }
        sentiance.invokeDummyVehicleCrash()
        resolve(true)
    }

    private func ensureSDKInitialized(_ reject: RCTPromiseRejectBlock) -> Bool {
        guard sentiance.initState == SENTSDKInitState.initialized else {
            reject(sdkNotInitializedError.code, sdkNotInitializedError.message, nil)
            return false
        }
        return true
    }

    @objc
    static func requiresMainQueueSetup() -> Bool {
        return false
    }
}
