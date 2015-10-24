//
//  MasterViewController.swift
//  cnbeta
//
//  Created by xeodou on 10/21/15.
//  Copyright (c) 2015 xeodou.me. All rights reserved.
//

import UIKit
import Cb

class MasterViewController: UITableViewController, GoCbListener {

    var detailViewController: DetailViewController? = nil

    var cbRss = GoCbNewRssXml()


    override func awakeFromNib() {
        super.awakeFromNib()
        if UIDevice.currentDevice().userInterfaceIdiom == .Pad {
            self.clearsSelectionOnViewWillAppear = false
            self.preferredContentSize = CGSize(width: 320.0, height: 600.0)
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        self.refreshControl = UIRefreshControl()
        self.refreshControl!.attributedTitle = NSAttributedString(string: "加载中...")
        self.refreshControl!.addTarget(self, action: "refresh:", forControlEvents: UIControlEvents.ValueChanged)
        self.tableView.addSubview(refreshControl!)
        self.refreshControl!.beginRefreshing()
       loadRss()
        
        if let split = self.splitViewController {
            let controllers = split.viewControllers
            self.detailViewController = controllers[controllers.count-1].topViewController as? DetailViewController
        }
    }
    
    func refresh(sender:AnyObject) {
       loadRss()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Segues

    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if segue.identifier == "showDetail" {
            if let indexPath = self.tableView.indexPathForSelectedRow() {
                
                let object = cbRss.channel().item(Int64(indexPath.row))
                let controller = (segue.destinationViewController as! UINavigationController).topViewController as! DetailViewController
                controller.detailItem = object
                controller.navigationItem.leftBarButtonItem = self.splitViewController?.displayModeButtonItem()
                controller.navigationItem.leftItemsSupplementBackButton = true
            }
        }
    }

    // MARK: - Table View

    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return Int(cbRss.channel().length())
    }

    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("Cell", forIndexPath: indexPath) as! UITableViewCell

        let object = cbRss.channel().item(Int64(indexPath.row))
        cell.textLabel!.text = object.title()
        cell.detailTextLabel!.text = object.description()
        return cell
    }

    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }

    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        
    }
    
    func loadRss() {
        let cnbeta = GoCbNewCnBeta(self)
        cnbeta.run()
    }
    
    // MARK: - cnbeta lisener interface
    
    func onEnd() {
        
    }
    
    func onFailure(err: String!) {
        var alert = UIAlertController(title: "Something wrong", message: err, preferredStyle: UIAlertControllerStyle.Alert)
        alert.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.Default, handler: nil))
        self.presentViewController(alert, animated: true, completion: nil)
    }
    
    func onSuccess(rss: GoCbRssXml!) {
        cbRss = rss
        
        dispatch_async(dispatch_get_main_queue(), {
            
            if (self.refreshControl!.refreshing) {
                self.refreshControl!.endRefreshing()
            }
            self.tableView.reloadData()
            
            // Masquer l'icône de chargement dans la barre de status
//            UIApplication.sharedApplication().networkActivityIndicatorVisible = false
        })
    }

}

