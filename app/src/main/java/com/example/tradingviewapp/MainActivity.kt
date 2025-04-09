  package com.example.tradingviewapp

  import android.os.Bundle
  import android.widget.Button
  import android.widget.EditText
  import android.widget.TextView
  import androidx.appcompat.app.AppCompatActivity
  import retrofit2.*
  import retrofit2.converter.gson.GsonConverterFactory
  import retrofit2.http.GET
  import retrofit2.http.Query

  class MainActivity : AppCompatActivity() {

      private val apiKey = "cvqqd89r01qp88cn4oagcvqqd89r01qp88cn4ob0"

      override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          setContentView(R.layout.activity_main)

          val symbolInput = findViewById<EditText>(R.id.symbolInput)
          val fetchButton = findViewById<Button>(R.id.fetchButton)
          val resultText = findViewById<TextView>(R.id.resultText)

          val retrofit = Retrofit.Builder()
              .baseUrl("https://finnhub.io/api/v1/")
              .addConverterFactory(GsonConverterFactory.create())
              .build()

          val service = retrofit.create(FinnhubService::class.java)

          fetchButton.setOnClickListener {
              val symbol = symbolInput.text.toString().uppercase()

              service.getTechnicalIndicators(symbol, apiKey, "1", "sma")
                  .enqueue(object : Callback<TechnicalIndicatorResponse> {
                      override fun onResponse(
                          call: Call<TechnicalIndicatorResponse>,
                          response: Response<TechnicalIndicatorResponse>
                      ) {
                          val indicators = response.body()
                          if (indicators != null && indicators.sma.isNotEmpty()) {
                              resultText.text = "SMA: ${indicators.sma.last()}"
                          } else {
                              resultText.text = "Veri bulunamadı."
                          }
                      }

                      override fun onFailure(call: Call<TechnicalIndicatorResponse>, t: Throwable) {
                          resultText.text = "Hata: ${t.message}"
                      }
                  })
          }
      }
  }

  interface FinnhubService {
      @GET("indicator")
      fun getTechnicalIndicators(
          @Query("symbol") symbol: String,
          @Query("token") token: String,
          @Query("resolution") resolution: String,
          @Query("indicator") indicator: String
      ): Call<TechnicalIndicatorResponse>
  }

  data class TechnicalIndicatorResponse(
      val sma: List<Float>
  )
