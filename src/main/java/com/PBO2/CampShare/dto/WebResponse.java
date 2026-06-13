package com.PBO2.CampShare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebResponse<T> {
    private Integer code;       // Contoh: 200 (Sukses), 400 (Gagal)
    private String status;      // Contoh: "OK" atau "BAD REQUEST"
    private T data;             // Tempat menaruh data barang/user
    private String errors;      // Pesan error jika ada yang salah
}
