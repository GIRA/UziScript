﻿using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace Simulator
{
    public partial class Pin : UserControl
    {
        private int number = 0;
        private short value = 0;
        private Sketch sketch;

        public int Number { get { return number; } }

        public Pin(int number, Sketch sketch)
        {
            this.number = number;
            this.sketch = sketch;

            InitializeComponent();
        }

        private void Pin_Paint(object sender, PaintEventArgs e)
        {
            label1.Text = number.ToString();
        }

        private void numericUpDown1_ValueChanged(object sender, EventArgs e)
        {
            sketch.SetPinValue(number, Convert.ToInt16(numericUpDown1.Value));
        }

        public void UpdateValue()
        {
            value = sketch.GetPinValue(number);
            numericUpDown1.Value = value;
            graph.Add(value);
            led.Image = value > 0 ?
                Properties.Resources.on :
                Properties.Resources.off;
        }

        private void led_Click(object sender, EventArgs e)
        {
            sketch.SetPinValue(number, (short)(1023 - value));
        }
        
    }
}
