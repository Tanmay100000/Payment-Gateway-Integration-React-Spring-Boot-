import axios from "axios";
import { useCallback } from "react";


const API_BASE = import.meta.env.VITE_API_BASE_URL;

// Helper to dynamically load external script
const loadScript = (src) =>
  new Promise((resolve, reject) => {
    const script = document.createElement("script");
    script.src = src;
    script.onload = () => resolve(true);
    script.onerror = () => reject(new Error("Script load error"));
    document.body.appendChild(script);
  });

export default function PaymentPage() {

  const createOrder = useCallback(async () => {
    try {
      // ensure Razorpay checkout script is loaded
      if (!window.Razorpay) {
        await loadScript("https://checkout.razorpay.com/v1/checkout.js");
      }

      if (!window.Razorpay) {
        throw new Error("Razorpay SDK failed to load.");
      }

      // Amount to send to backend: in rupees (backend expects rupees)
      const amountInRupees = 1;

      // 1) Create order from backend (send amount in rupees)
      const res = await axios.post(
        `${API_BASE}/api/payment/create-order?amount=${amountInRupees}`
      );
 
      console.log("create-order response:", res.data);

      const order = res.data;
      if (!order || !order.id || !order.amount) {
        throw new Error("Invalid order returned from server");
      }
const RAZORPAY_KEY = import.meta.env.VITE_RAZORPAY_KEY || "rzp_test_fallback";

      const options = {
        key: RAZORPAY_KEY,
        amount: order.amount,
        currency: "INR",
        name: "My Test Store",
        description: "Test Transaction",
        order_id: order.id,

        handler: async function (response) {
          console.log("Razorpay Response:", response);

          try {
            const verifyRes = await axios.post(
              `${API_BASE}/api/payment/verify`,
              {
                orderId: response.razorpay_order_id,
                paymentId: response.razorpay_payment_id,
                signature: response.razorpay_signature,
              }
            );

            if (verifyRes.data && verifyRes.data.status === "SUCCESS") {
              await axios.post(`${API_BASE}/api/payment/save`, {
                orderId: response.razorpay_order_id,
                paymentId: response.razorpay_payment_id,
                signature: response.razorpay_signature,
                status: "SUCCESS",
                amount: order.amount / 100,
              });

              alert("Payment Successful!");
            } else {
              alert("Verification Failed!");
            }
          } catch (verifyErr) {
            console.error("Verification error:", verifyErr);
            alert("Error verifying payment");
          }
        },

        theme: { color: "#3399cc" },
      };

      const rzp = new window.Razorpay(options);

      rzp.on && rzp.on("payment.failed", function (response) {
        console.error("Payment failed:", response);
        alert("Payment failed. See console for details.");
      });

      rzp.open();

    } catch (err) {
      console.error("Payment Error:", err);
      alert("Error in payment: " + (err.message || err));
    }
  }, []);

  return (
    <div style={{ padding: "30px" }}>
      <h2>Razorpay Payment</h2>
      <button onClick={createOrder} style={{
        padding: "12px 20px",
        background: "black",
        color: "white",
        borderRadius: "5px",
        cursor: "pointer",
        fontSize: "16px"
      }}>
        Pay ₹1
      </button>
    </div>
  );
}
