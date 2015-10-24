//
//  DetailViewController.swift
//  cnbeta
//
//  Created by xeodou on 10/21/15.
//  Copyright (c) 2015 xeodou.me. All rights reserved.
//

import UIKit
import Cb

class DetailViewController: UIViewController {

    @IBOutlet var webView: UIWebView!

    var detailItem: GoCbRssItem? {
        didSet {
            // Update the view.
            self.configureView()
        }
    }
    
    override func loadView() {
        super.loadView()
    }

    func configureView() {
        // Update the user interface for the detail item.
        if let detail: GoCbRssItem = self.detailItem {
           self.title = detail.title()
        }
        
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        self.configureView()
        let url = NSURL(string: "http://gxc.google.com.hk/gwt/x?u=" + self.detailItem!.link())
        let request = NSURLRequest(URL: url!)
        webView.loadRequest(request)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}

