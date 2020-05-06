package TaobaoUnion.view.activity

import TaobaoUnion.viewmodel.TicketViewModel
import android.content.*
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.adgvcxz.add
import com.adgvcxz.toBind
import com.adgvcxz.toEventBind
import com.adgvcxz.viewmodel.sample.BaseActivity
import com.adgvcxz.viewmodel.sample.R
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.activity_ticket.*


class TicketActivity : BaseActivity() {

    private lateinit var ticketUrl: String
    private lateinit var ticketTitle: String
    private lateinit var ticketCover: String
    private val mTicketViewModel by lazy { TicketViewModel(ticketUrl, ticketTitle) }

    override val layoutId: Int = R.layout.activity_ticket

    override fun initBinding() {

        ticketUrl = intent.getStringExtra("ticket_url")
        ticketTitle = intent.getStringExtra("ticket_title")
        ticketCover = intent.getStringExtra("ticket_cover")

        mTicketViewModel.toEventBind(disposables) {
            add({ clicks() }, ticket_back_press, { TicketViewModel.Event.EndEvent })
            add({ clicks() }, ticket_copy_or_open_btn, { TicketViewModel.Event.TicketCodeEvent })
        }

        mTicketViewModel.toBind(disposables) {
            //淘口令加载
            add({ ticketCode }, { ticket_code.setText(this) })
            { filter { mTicketViewModel.currentModel().state == TicketViewModel.TimerStatus.Timing } }
            //淘口令界面图片加载
            add({ ticketCover }, { Glide.with(this@TicketActivity).load(ticketCover).into(ticket_cover) })
            //领券按钮文本显示
            add({ hasTaoBaoApp() }, {
                ticket_copy_or_open_btn.text = when(this){
                    true -> "打开淘宝领券"
                    false -> "复制口令"
                }
            })
            //结束TicketActivity
            add({ state }, { if (this == TicketViewModel.TimerStatus.Finish) finish() })
            //跳转到TaoBao
            add({ state }, { if (this == TicketViewModel.TimerStatus.Skip) ticketCodeEvent() })
        }

        mTicketViewModel.action.onNext(TicketViewModel.Event.StartEvent)
    }

    //判断是否安装TaoBaoApp
    private fun hasTaoBaoApp(): Boolean {
        val packManager = this.packageManager
        return try {
            val packageInfo = packManager.getPackageInfo("com.taobao.taobao",
                    PackageManager.MATCH_UNINSTALLED_PACKAGES)
            packageInfo != null
        } catch (e: Exception) {
            false
        }
    }


    //领券事件的具体逻辑
    private fun ticketCodeEvent() {
        //获取剪贴板管理器
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        //拿到淘口令内容
        val ticketCode = ticket_code.text.toString().trim()
        //复制到剪贴板
        val mClipData = ClipData.newPlainText("ticketCode", ticketCode)
        cm.primaryClip = mClipData
        if (hasTaoBaoApp()) {
            //跳转到淘宝
            val component = ComponentName("com.taobao.taobao", "com.taobao.tao.TBMainActivity")
            val intent = Intent()
            intent.component = component
            startActivity(intent)
            finish()
        } else {
            //提示已经复制了
            Toast.makeText(this, "已经复制,粘贴分享，或打开淘宝", Toast.LENGTH_LONG).show()
        }
    }

}