Pod::Spec.new do |s|
    s.name              = 'SENTSDK'
    s.version           = '5.1.8-CN'
    s.summary           = 'The Sentiance iOS SDK.'
    s.homepage          = 'https://sentiance.com/'

    s.author            = { 'Name' => 'sdk@sentiance.com' }
    s.license           = { :type => 'MIT' }
    s.platform          = :ios
    s.source            = { :http => 'https://s3.cn-north-1.amazonaws.com.cn/sentiance-sdk/ios/transport/cn/SENTSDK-5.1.8-CN.framework.zip' }
    s.ios.deployment_target = '8.0'
    s.frameworks = 'CoreMotion', 'SystemConfiguration', 'CoreLocation', 'Foundation', 'CallKit', 'CoreTelephony', 'CoreData'
    s.libraries = 'z'
    s.compiler_flags = '-lz', '-all_load', 'lc++'
    s.resources = '**/SENTSDK.bundle'
    s.vendored_frameworks = 'SENTSDK.framework'
end
