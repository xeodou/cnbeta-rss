/*
* @Author: xeodou
* @Date:   2015
* @Last Modified by:   xeodou
* @Last Modified time: 2015
 */

package cb

import (
	"fmt"
	"sync"
	"testing"
)

const (
	count = 1
)

var cache *RssXml
var wg sync.WaitGroup

type Tmp struct {
}

func (t *Tmp) OnSuccess(rss *RssXml) {
	cache = rss
}
func (t *Tmp) OnFailure(err string) {
	fmt.Println(err)
}

func (t *Tmp) OnEnd() {
	wg.Done()
}

func TestLoadRss(t *testing.T) {
	wg.Add(1)

	tmp := &Tmp{}
	cb := NewCb()
	cb.AddListener(tmp)
	cb.Run()

	wg.Wait()

	if cache.Version == "" ||
		cache.Channel.Title == "" ||
		cache.Channel.Length() <= 0 {
		t.Errorf("load rss feed failed.")
	}

}
