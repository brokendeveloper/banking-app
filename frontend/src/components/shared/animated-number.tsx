"use client";

import { useEffect, useRef } from "react";
import { motion, useMotionValue, useTransform, animate } from "framer-motion";

interface AnimatedNumberProps {
  value: number;
  formatter?: (v: number) => string;
  className?: string;
}

export function AnimatedNumber({
  value,
  formatter = (v) => v.toFixed(2),
  className,
}: AnimatedNumberProps) {
  const motionValue = useMotionValue(0);
  const displayValue = useTransform(motionValue, (v) => formatter(v));
  const prevValue = useRef(0);

  useEffect(() => {
    const controls = animate(motionValue, value, {
      duration: 1.2,
      ease: [0.16, 1, 0.3, 1],
    });
    prevValue.current = value;
    return controls.stop;
  }, [value, motionValue]);

  return (
    <motion.span className={className}>{displayValue}</motion.span>
  );
}
