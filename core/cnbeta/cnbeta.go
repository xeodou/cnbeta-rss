/*
* @Author: xeodou
* @Date:   2015
* @Last Modified by:   xeodou
* @Last Modified time: 2015
 */

package cb

import (
	xmlx "github.com/jteeuwen/go-pkg-xmlx"
	async "github.com/xeodou/go-async"
)

func RSS() string {
	return "http://rss.cnbeta.com/rss"
}

type Listener interface {
	OnSuccess(rss *RssXml)
	OnFailure(err string)
	OnEnd()
}

type CnBeta struct {
	listener Listener
}

func NewCnBeta(l Listener) *CnBeta {
	return &CnBeta{l}
}

func NewCb() *CnBeta {
	return &CnBeta{}
}

func (cb *CnBeta) AddListener(l Listener) {
	cb.listener = l
}

func (cb *CnBeta) Run() {
	t := async.NewTask(RSS(), cb)
	t.Runtask()
}

func (cb *CnBeta) Success(buf []byte) {
	doc := xmlx.New()
	if err := doc.LoadBytes(buf, nil); err != nil {
		cb.Failure(err)
		return
	}
	cb.listener.OnSuccess(parser(doc))
	cb.listener.OnEnd()
}

func (cb *CnBeta) Failure(err error) {
	cb.listener.OnFailure(err.Error())
	cb.listener.OnEnd()
}

func parser(doc *xmlx.Document) *RssXml {
	rss := &RssXml{
		Version: "2.0",
	}
	const ns = "*"
	node := doc.SelectNode(ns, "rss")
	channel := node.SelectNode(ns, "channel")

	feed := &RssFeed{}

	feed.Title = channel.S(ns, "title")
	feed.Link = channel.S(ns, "link")
	feed.Description = channel.S(ns, "description")
	feed.Language = channel.S(ns, "language")
	feed.Copyright = channel.S(ns, "copyright")
	feed.PubDate = channel.S(ns, "pubDate")

	items := channel.SelectNodes(ns, "item")
	for _, i := range items {
		item := &RssItem{}
		item.Title = i.S(ns, "title")
		item.Link = i.S(ns, "link")
		item.Description = i.S(ns, "description")
		item.PubDate = i.S(ns, "pubDate")
		feed.items = append(feed.items, item)
	}
	rss.Channel = feed

	return rss
}

func NewRssXml() *RssXml {
	return &RssXml{
		Channel: &RssFeed{},
	}
}

type RssXml struct {
	Version string
	Channel *RssFeed
}

type RssFeed struct {
	Title       string
	Link        string
	Description string
	Language    string
	Copyright   string
	PubDate     string

	items []*RssItem
}

type RssItem struct {
	Title       string
	Link        string
	Description string
	PubDate     string
}

func (rss *RssXml) Feed() *RssFeed {
	return rss.Channel
}

func (feed *RssFeed) Length() int64 {
	return int64(len(feed.items))
}

func (feed *RssFeed) Item(p int64) *RssItem {
	if p >= feed.Length() {
		return &RssItem{}
	}
	return feed.items[p]
}
