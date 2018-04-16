Pod::Spec.new do |s|  
    s.name              = 'SentianceSDK'
    s.version           = '1.0.0'
    s.summary           = 'The Sentiance iOS SDK.'
    s.homepage          = 'https://sentiance.com/'

    s.author            = { 'Name' => 'sdk@sentiance.com' }
    s.license           = { :type => 'MIT' }
    s.platform          = :ios
    s.source            = { :http => 'https://s3-eu-west-1.amazonaws.com/sentiance-sdk/ios/transport/SENTTransportDetectionSDK-4.6.13.framework.zip' }

    s.ios.deployment_target = '8.0'
    s.ios.vendored_frameworks = 'SENTTransportDetectionSDK.framework'
    s.frameworks = 'CoreMotion', 'SystemConfiguration', 'CoreLocation', 'Foundation', 'CallKit', 'CoreTelephony'
    s.libraries = 'z'
    s.compiler_flags = '-lz', '-all_load'
end  
