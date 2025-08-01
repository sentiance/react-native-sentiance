require 'json'
package = JSON.parse(File.read(File.join(__dir__, './package.json')))
sentiance_sdk_package_version = package['sdkVersions']['ios']['sentiance']
sentiance_sdk_env_var_version = ENV["SENTIANCE_RN_IOS_SDK_VERSION"]

Pod::Spec.new do |s|
  s.name         = "RNSentianceCore"
  s.version      = "6.10.1"
  s.summary      = "RNSentianceCore"
  s.description  = <<-DESC
                   RNSentianceCore
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
  s.static_framework = true
  s.pod_target_xcconfig = { "DEFINES_MODULE" => "YES" }
  s.dependency "React"

  if sentiance_sdk_env_var_version.nil?
    s.dependency "SENTSDK", sentiance_sdk_package_version
  else
    s.dependency "SENTSDK", sentiance_sdk_env_var_version
  end

end
