import SwiftUI
import UIKit

@main
struct iOSApp: App {
    init() {
        _ = UIFont.systemFont(ofSize: 12)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}