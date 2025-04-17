Pod::Spec.new do |s|
  s.name         = "SentianceCrashDetection"
  s.version      = "6.13.0-rc.4"
  s.summary      = "SentianceCrashDetection"
  s.description  = <<-DESC
                   SentianceCrashDetection
                   DESC
  s.homepage     = "https://developers.sentiance.com/docs"
  s.license      = "MIT"
  s.author       = { "author" => "sdk@sentiance.com" }
  s.platform     = :ios, "9.0"
  s.source       = { :path => '.' }
  s.source_files = "ios/**/*.{h,m,swift}"
  s.requires_arc = true
  s.xcconfig     = { 'FRAMEWORK_SEARCH_PATHS' => '${PODS_ROOT}/SENTSDK' }
  s.swift_version = '5.0'
  s.dependency "React"
  s.dependency "RNSentianceCore"
end
