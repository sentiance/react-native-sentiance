require 'json'
corePackage = JSON.parse(File.read(File.join('..', 'core', 'package.json')))
sentiance_sdk_package_version = corePackage['sdkVersions']['ios']['sentiance']
sentiance_sdk_env_var_version = ENV["SENTIANCE_RN_IOS_SDK_VERSION"]

Pod::Spec.new do |s|
  s.name         = "SentianceCrashDetection"
  s.version      = "6.12.0-alpha.8"
  s.summary      = "SentianceCrashDetection"
  s.description  = <<-DESC
                   SentianceCrashDetection
                   DESC
  s.homepage     = "https://developers.sentiance.com/docs"
  s.license      = "MIT"
  # s.license    = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author       = { "author" => "sdk@sentiance.com" }
  s.platform     = :ios, "9.0"
  s.source       = { :path => '.' }
  s.source_files = "ios/**/*.{h,m,swift}"
  s.requires_arc = true
  s.xcconfig     = { 'FRAMEWORK_SEARCH_PATHS' => '${PODS_ROOT}/SENTSDK' }
  s.swift_version = '5.0'
  s.dependency "React"

  if sentiance_sdk_env_var_version.nil?
    s.dependency "SENTSDK", sentiance_sdk_package_version
  else
    s.dependency "SENTSDK", sentiance_sdk_env_var_version
  end

end
