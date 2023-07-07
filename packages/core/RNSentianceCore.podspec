require 'json'
package = JSON.parse(File.read(File.join(__dir__, './package.json')))
sentiance_sdk_version = package['sdkVersions']['ios']['sentiance']

Pod::Spec.new do |s|
  s.name         = "RNSentianceCore"
  s.version      = "6.4.0-rc.3"
  s.summary      = "RNSentianceCore"
  s.description  = <<-DESC
                   RNSentianceCore
                   DESC
  s.homepage     = "https://developers.sentiance.com/docs"
  s.license      = "MIT"
  # s.license    = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author       = { "author" => "sdk@sentiance.com" }
  s.platform     = :ios, "9.0"
  s.source       = { :git => "https://github.com/sentiance/react-native-sentiance.git", :tag => "v#{s.version}" }
  s.source_files = "packages/core/ios/*.{h,m}"
  s.requires_arc = true
  s.xcconfig     = { 'FRAMEWORK_SEARCH_PATHS' => '${PODS_ROOT}/SENTSDK' }

  s.dependency "React"
  s.dependency "SENTSDK", sentiance_sdk_version
end
