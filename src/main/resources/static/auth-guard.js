// auth-guard.js (Versi Super Ketat)
(function () {
    const idUser = localStorage.getItem("idUser");

    // Cek apakah idUser benar-benar kosong, atau berisi teks "null"/"undefined"
    if (!idUser || idUser === "null" || idUser === "undefined" || idUser.trim() === "") {
        alert("Akses ditolak! Anda harus login terlebih dahulu.");
        window.location.href = "login";
    }
})();