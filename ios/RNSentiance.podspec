Pod::Spec.new do |s|
  s.name         = "RNSentiance"
  s.version      = "4.4.1"
  s.summary      = "RNSentiance"
  s.description  = <<-DESC
                   RNSentiance
                   DESC
  s.homepage     = "https://developers.sentiance.com/docs"
  s.license      = "MIT"
  # s.license    = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author       = { "author" => "sdk@sentiance.com" }
  s.platform     = :ios, "9.0"
  s.source       = { :git => "https://github.com/sentiance/react-native-sentiance.git", :tag => "master" }
  s.source_files = "*.{h,m}"
  s.requires_arc = true
  s.xcconfig     = { 'FRAMEWORK_SEARCH_PATHS' => '${PODS_ROOT}/SENTSDK' }

  s.dependency "React"
  s.dependency "SENTSDK", "5.10.0"
end
