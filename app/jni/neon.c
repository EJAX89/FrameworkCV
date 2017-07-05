//
// Created by Aleš on 03.08.2016.
//
#include <arm_neon.h>

public static void SepiaFilter(WriteableBitmap Bitmap){
for (var i = 0; i < Bitmap.Pixels.Length; ++i){
var pixel = Bitmap.Pixels[i];

//vytazeni cerveneho, zeleneho a modreho elementu
var ir = (pixel & 0xFF0000) >> 16;
var ig = (pixel & 0xFF00) >> 8;
var ib = pixel & 0xFF;

//aplikace transformace
var or = (uint)(ir * 0.393f + ig * 0.769f + ib * 0.189f);
var og = (uint)(ir * 0.349f + ig * 0.686f + ib * 0.168f);
var ob = (uint)(ir * 0.272f + ig * 0.534f + ib * 0.131f);

//Saturace vysledku
or = or > 255 ? 255 : or;
og = og > 255 ? 255 : og;
ob = ob > 255 ? 255 : ob;

// Zapis vyslednych pixelu do bitmapy
Bitmap.Pixels[i] = (int)(0xFF000000 | or << 16 | og << 8 | ob);
}
}

public void rgb_to_gray(const uint8_t* rgb, uint8_t* gray, int num_pixels)
 {
 for(int i=0; i<num_pixels; ++i, rgb+=3)
  {
    int v = (77*rgb[0] + 150*rgb[1] + 29*rgb[2]);         gray[i] = v>>8;
     }
  }
public void rgb_to_gray_neon(const uint8_t* rgb, uint8_t* gray, int num_pixels)
{
num_pixels /= 8;
uint8x8_t w_r = vdup_n_u8(77);
uint8x8_t w_g = vdup_n_u8(150);
uint8x8_t w_b = vdup_n_u8(29);

//ukladani mezivysledku
uint16x8_t temp;

//vysledky v odstinech sedi
uint8x8_t result;
for(int i=0; i<num_pixels; ++i, rgb+=8*3, gray+=8)
{
uint8x8x3_t src = vld3_u8(rgb);
temp = vmull_u8(src.val[0], w_r);
temp = vmlal_u8(temp, src.val[1], w_g);
temp = vmlal_u8(temp, src.val[2], w_b);

result = vshrn_n_u16(temp, 8);

vst1_u8(gray, result);
}
}